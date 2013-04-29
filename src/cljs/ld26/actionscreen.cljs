(ns ld26.actionscreen
  (:require [ld26.image :as gimage]
            [ld26.state :as gstate]
            [ld26.dialog]
            [ld26.item :as gitem]
            [ld26.data :as gdata]
            [ld26.sound :as gsound]
            [ld26.ending])
)

(def examine-pos [655 456])
(def take-pos [620 499])
(def combine-pos [640 545])
(def up-arr-pos [585 470])
(def down-arr-pos [585 540])
(def *perm-screen-states* {})

(defn handle-input [screen mouse]
  (let [x (:x mouse)
        y (:y mouse)
        clicked (:clicked mouse)]
    (assoc screen :mouse {:x x :y y :clicked clicked})
  )
)

(defn maybe-handle-input [screen on-top mouse]
  (if on-top
    (handle-input screen mouse)
    screen
  )
)

(defn inside-rectangle? [pos1 pos2 key]
  (let [x1 (first pos1)
        y1 (second pos1)
        x2 (first pos2)
        y2 (second pos2)
        width (.-width (key @gimage/*image-map*))
        height (.-height (key @gimage/*image-map*))]
    (and (>= x2 x1)
         (<= x2 (+ x1 width))
         (>= y2 y1)
         (<= y2 (+ y1 height)))
  )
)

(defn inside-area? [item pos key]
  (inside-rectangle? (:position item) pos (key item))
)

(defn hover-items [items pos key]
  (filter #(inside-area? % pos key) items)
)

(defn get-hover [screen]
  (let [mousepos [(:x (:mouse screen)) (:y (:mouse screen))]
        inv (first (hover-items (:displayed-inv screen)
                                mousepos :inventory-image))
        items (first (hover-items (:items screen) mousepos :image))
        exits (first (hover-items (:exits screen) mousepos :image))]
    (cond 
      (not (nil? inv)) (assoc screen :hovertext (:hover-text inv))
      (not (nil? items)) (assoc screen :hovertext (:hover-text items))
      (not (nil? exits)) (assoc screen :hovertext (:hover-text exits))
      :else (assoc screen :hovertext "")
    )
  )
)

(defn popup-dialog [popup-text canvas ctx]
  (if (nil? popup-text)
    nil
    (fn [state]
      (let [screen-list (:screen-list state)
            dialog (ld26.dialog/init ctx canvas [] popup-text)
            new-list (gstate/push-screen dialog screen-list)]
        (assoc state :screen-list new-list)
      )
    )
  )
)

(defn do-pickup [screen item inventory]
  (let [ctx (:context screen)
        canvas (:canvas screen)]
    (assoc screen :inventory (gitem/pick-up inventory item)
                  :items (filter #(not (= (:id item) (:id %))) (:items screen))
                  :next-frame (popup-dialog (:pickup-text item) canvas ctx))
  )
)

(defn try-pickup [screen pos]
  (let [sel-item (first (hover-items (:items screen) pos :image))
        inventory (:inventory screen)]
    (if (and (not (nil? sel-item))
             (not (nil? (gitem/pick-up inventory sel-item))))
      (do-pickup screen sel-item inventory)
      screen
    )
  )
)

(defn down-arrow-clicked [screen]
  (let [depth (:inv-depth screen)
        elems (count (:inventory screen))]
    (if (> (int (/ elems 4)) depth)
      (assoc screen :inv-depth (inc depth))
      screen
    )
  )
)

(defn up-arrow-clicked [screen]
  (let [depth (:inv-depth screen)]
    (if (> depth 0)
      (assoc screen :inv-depth (dec depth))
      screen
    )
  )
)

(defn try-buttons [screen pos]
  (cond
    (inside-rectangle? examine-pos pos :examine-norm) 
      (assoc screen :sel-button 0 :sel-item "")
    (inside-rectangle? take-pos pos :take-norm)
      (assoc screen :sel-button 1 :sel-item "")
    (inside-rectangle? combine-pos pos :combine-norm)
      (assoc screen :sel-button 2)
    (inside-rectangle? up-arr-pos pos :up-arr)
      (up-arrow-clicked screen)
    (inside-rectangle? down-arr-pos pos :down-arr)
      (down-arrow-clicked screen)
    :else 
      screen
  )
)

(defn combine-item-callback [coll id]
  (if (or (nil? id) (empty? id))
    coll
    (reverse (conj (reverse coll) (gitem/create-item id)))
  )
)

(defn combine-exit-callback [exits id]
  exits
)

(declare init)
(defn use-exit-callback [screen id stype]
  (let [ctx (:context screen)
        canvas (:canvas screen)]
    (if (nil? stype)
      (do 
        (set! *perm-screen-states* 
              (conj *perm-screen-states* { (:id screen) screen })
        )
        (assoc screen 
               :next-frame
               (fn [state]
                 (let [screen-list (:screen-list state)
                       ascreen (init ctx canvas 
                                     (:inventory screen) id)
                       new-list (gstate/replace-screen ascreen screen-list)]
                   (assoc state :screen-list new-list)
                 )
               )
        )
      )
      (assoc screen 
             :next-frame
             (fn [state]
               (let [screen-list (:screen-list state)
                     escreen (ld26.ending/init ctx canvas
                                               gdata/final-scene)
                     new-list (gstate/replace-screen escreen screen-list)]
                 (assoc state :screen-list new-list)
                )
              )
      )
    )
  )
)

(defn combine-pocket-items [inventory item1 item2]
  (let [ev1 (:generate-events item1)
        ev2 (:generate-events item2)]
    (reduce #(%2 %1 combine-item-callback) 
            (reduce #(%2 %1 combine-item-callback) inventory ev1) ev2)
  )
)

(defn combine-items [inventory screen item1 item2]
  (let [ev1 (:generate-events item1)
        ev2 (:generate-events item2)]
    (assoc screen :inventory 
                  (reduce #(%2 %1 combine-item-callback) inventory ev1)
                  :items 
                  (reduce #(%2 %1 combine-item-callback) (:items screen) ev2)
                  :sel-item "")
  )
)

(defn combine-exit [inventory screen item exit]
  (let [ev1 (:generate-events item)
        ev2 (:generate-events exit)]
    (assoc screen :inventory (reduce #(%2 %1 combine-item-callback)
                                    inventory  ev1)
                  :exits (reduce #(%2 %1 combine-exit-callback) exit ev2))
  )
)

(defn try-select-one [screen pos]
  (let [inv (:displayed-inv screen)
        selected (filter #(inside-area? % pos :inventory-image) inv)]
    (if (empty? selected)
       screen
      (assoc screen :sel-item (:id (first selected)))
    )
  )
)

(defn use-exit [screen exit]
  (reduce #(%2 %1 use-exit-callback exit) screen (:generate-events exit))
)

(defn try-use-exit [screen pos]
  (let [exits (:exits screen)
        items (:items screen)
        selected (first (filter #(inside-rectangle? (:position %)
                                                     pos (:image %))
                                exits))
        selected-item (first (filter #(inside-rectangle? (:position %)
                                                         pos (:image %))
                                    items)) ]
    (if (and (not (nil? selected)) (nil? (:combine-with selected))
             (nil? selected-item))
      (use-exit screen selected)
       screen
    )
  )
)

(defn try-select-two [screen pos]
  (let [inv (:displayed-inv screen)
        inv-real (:inventory screen)
        item1 (filter #(= (:id %) (:sel-item screen)) inv-real)
        item2 (filter #(inside-area? % pos :inventory-image) inv)]
    (if (and (not (empty? item1)) (not (empty? item2))
             (= (:combine-with (first item1)) (:id (first item2)))
             (= (:combine-with (first item2)) (:id (first item1))))
      (assoc screen :inventory (combine-pocket-items inv-real 
                                                    (first item1) 
                                                    (first item2))
                    :sel-item "")
      screen
    )
  )
)

(defn make-combine-items [inv screen item1 item2]
  (let [new-screen (combine-items inv screen item1 item2)
        new-items (remove (set inv) (:inventory new-screen))]
    (assoc new-screen 
           :next-frame (popup-dialog (:pickup-text (first new-items))
                                     (:canvas new-screen)
                                     (:context new-screen))
    )
  )
)
    

(defn try-combine-exit [screen pos]
  (let [inv (:inventory screen)
        item1 (first (filter #(= (:id %) (:sel-item screen)) inv))
        item2 (first (filter #(inside-area? % pos :image) (:items screen)))
        exit (first (filter #(inside-area? % pos :image) (:exits screen)))]
    (cond
      (not (nil? item2)) (if (and (= (:combine-with item1) (:id item2))
                                  (not (:pickupable item2)))
                           (make-combine-items inv screen item1 item2)
                           screen)
      (not (nil? exit)) (if (= (:combine-with exit) (:id item1))
                          (combine-exit inv screen item1 exit)
                          screen)
      :else screen
    )
  )
)

(defn try-select-inv [screen pos func1 func2]
  (if (and (>= (first pos) 0)
           (<= (first pos) 800)
           (>= (second pos) 450)
           (<= (second pos) 600))
    (func1 screen pos)
    (func2 screen pos)
  )
)

(defn try-combine [screen pos]
  (if (empty? (:sel-item screen))
    (try-select-inv screen pos try-select-one try-use-exit)
    (try-select-inv screen pos try-select-two try-combine-exit)
  )
)

(defn do-examine [screen item]
  (let [ctx (:context screen)
        canvas (:canvas screen)
        examine-text (:examine-text item)]
    (assoc screen :next-frame (popup-dialog examine-text canvas ctx))
  )
)

(defn try-examine [screen pos]
  (let [sel-item (first (hover-items (:items screen) pos :image))
        sel-exit (first (hover-items (:exits screen) pos :image))
        sel-inv-item (first (hover-items (:displayed-inv screen)
                                         pos :inventory-image))]
    (cond
      (not (nil? sel-inv-item)) (do-examine screen sel-inv-item)
      (not (nil? sel-item)) (do-examine screen sel-item)
      (not (nil? sel-exit)) (do-examine screen sel-exit)
      :else screen
    )
  )
)

(defn try-action [screen pos]
  (let [sel-button (:sel-button screen)]
    (cond 
      (= 0 sel-button) (try-examine screen pos)
      (= 1 sel-button) (try-pickup screen pos)
      (= 2 sel-button) (try-combine screen pos)
      :else screen
    )
  )
)

(defn check-click [screen]
  (let [x (:x (:mouse screen))
        y (:y (:mouse screen))
        clicked (:clicked (:mouse screen))]
    (if clicked
      (-> screen
        (try-action [x y])
        (try-buttons [x y])
      )
      screen
    )
  )
)

; This function makes sure all items in inventory are sorted
(defn sort-items [inventory depth]
  (map-indexed 
    (fn [n s]
      (let [basex 20
            basey 450
            sizex 140
            row  (int (/ n 4))
            col (mod n 4)]
        (assoc s :position [(+ (* col sizex) basex )
                            (+ basey (* (- row depth) basey))])
      )
    )
    inventory
  )
)

(defn get-limited-inv [inventory depth]
  (if (<= (count inventory) 4)
    inventory
    (subvec inventory (* depth 4) (dec (* (inc depth) 4)))
  )
)

(defn update-inventory [screen]
  (let [inv (filter #(not (nil? %))
                    (sort-items (:inventory screen) (:inv-depth screen)))
        lim-inv (get-limited-inv inv (:inv-depth screen))]
    (assoc screen :inventory inv
                  :displayed-inv lim-inv)
  )
)

(defn do-first-speech [screen]
  (let [ctx (:context screen)
        canvas (:canvas screen)]
    (if (not (empty? (:first-speech screen)))
      (assoc screen :visited true
                  :next-frame (popup-dialog (:first-speech screen) canvas ctx))
      (assoc screen :visited true)
    )
  )
)

(defn maybe-first-speech [screen]
  (if (not (:visited screen))
    (do-first-speech screen)
    screen
  )
)


(defn update [screen elapsed-time]
  (-> screen
    (get-hover)
    (check-click)
    (update-inventory)
    (maybe-first-speech)
  )
)

(defn maybe-update [screen on-top elapsed-time]
  (if on-top
    (update screen elapsed-time)
    screen
  )
)

(defn draw-arrows [ctx]
  (gimage/draw-image ctx up-arr-pos :up-arr)
  (gimage/draw-image ctx down-arr-pos :down-arr)
)

(defn draw-inventory [inventory sel-button sel-item ctx]
  (gimage/draw-image ctx [0 450] :inventorybase)
  (if (not (empty? sel-item))
    (let [item (first (filter #(= (:id %) sel-item) inventory))]
      (if (and (not (nil? item)) (not (empty? item)))
        (gimage/draw-image ctx (:position item) :selected-item)
        nil
      )
    )
    nil
  )
  (doall (map #(gitem/draw-item-inventory % ctx) inventory))
  (if (= 0 sel-button)
    (gimage/draw-image ctx [655 456] :examine-sel)
    (gimage/draw-image ctx [655 456] :examine-norm)
  )
  (if (= 1 sel-button)
    (gimage/draw-image ctx [620 499] :take-sel)
    (gimage/draw-image ctx [620 499] :take-norm)
  )
  (if (= 2 sel-button)
    (gimage/draw-image ctx [640 545] :combine-sel)
    (gimage/draw-image ctx [640 545] :combine-norm)
  )
  (draw-arrows ctx)
)

(defn draw-items [itemlist ctx]
  (doall (map #(gitem/draw-item % ctx) (reverse itemlist)))
)

(defn draw-exits [exitlist ctx]
  (doall (map #(gitem/draw-exit % ctx) (reverse exitlist)))
)

(defn draw-hovertext [text mouse-coords ctx]
  (if (empty? text)
    nil
    (let [x (:x mouse-coords)
          y (:y mouse-coords)]
      (gimage/draw-multiline-center-text ctx [x (+ y 15)] text
                                         "15px" "red" 20 20)
    )
  )
)

(defn draw [screen]
  (let [bg (:background screen)
        ctx (:context screen)
        sel-button (:sel-button screen)
        sel-item (:sel-item screen)]
    (gimage/draw-image ctx [0 0] bg)
    (draw-exits (:exits screen) ctx)
    (draw-items (:items screen) ctx)
    (draw-inventory (:displayed-inv screen) sel-button sel-item ctx)
    (draw-hovertext (:hovertext screen) (:mouse screen) ctx)
  )
  screen
)

(defn generate-item-list [items]
  (map (fn [item]
         (let [pos [(:x item) (:y item)]] 
           (assoc (gitem/create-item (:id item)) :position pos)
         )
       )
       items
  )
)

(defn generate-exit-list [exits]
  (map (fn [exit]
         (let [pos [(:x exit) (:y exit)]]
           (assoc (gitem/create-exit (:id exit)) :position pos)
         )
       )
       exits
  )
)

(defn maybe-restore-screen [screen id]
  (if (not (nil? (id *perm-screen-states*)))
    (into screen (id *perm-screen-states*))
    screen
  )
)

(defn init [ctx canvas inventory id]
  (-> gstate/*base-screen*
    (into {
           :id id
           :update maybe-update
           :render (fn [a b] (draw a))
           :handle-input maybe-handle-input
           :next-frame nil
           :background :testbg
           :context ctx
           :canvas canvas
           :images []
           :deinit (fn [s] nil)
           :mouse {:x 0 :y 0 :clicked false}
           :items []
           :inventory []
           :displayed-inv []
           :inv-depth 0
           :hovertext ""
           :sel-button 0 ; 0 = examine, 1 = take, 2 = combine
           :sel-item ""
           :exits []
           :visited false
           :first-speech nil

          }
    )
    (into (id gdata/*action-screens*))
    (#(assoc % :id id))
    (#(assoc % :items (generate-item-list (:items %))))
    (#(assoc % :exits (generate-exit-list (:exits %))))
    (maybe-restore-screen id)
    (assoc :inventory inventory)
    (gsound/screen-start-bgm)
  )
)
