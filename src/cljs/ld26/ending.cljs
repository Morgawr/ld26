(ns ld26.ending
  (:require [ld26.image :as gimage]
            [ld26.state :as gstate]
            [ld26.sound :as gsound]
            [ld26.dialog]
            ;[ld26.mainmenu]
  )
)

(defn check-scene [screen on-top]
  (if on-top
    (assoc screen :current (first (:scenes screen))
                  :scenes (vec (rest (:scenes screen))))
    screen 
  )
)

(defn start-new-game [screen]
  (let [ctx (:context screen)
        canvas (:canvas screen)]
    (assoc screen
           :next-frame
           (fn [state]
             (let [screen-list (:screen-list state)
                   new-list (gstate/pop-screen screen-list)]
               (assoc state :screen-list new-list)
             )
           )
    )
  )
)

(defn popup-dialog [screen]
  (let [ctx (:context screen)
        canvas (:canvas screen)
        popup-text (:messages (:current screen))]
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
)

(defn update [screen on-top elapsed-time]
  (let [new-screen (check-scene screen on-top)]
    (cond
      (not (= (:background (:current screen)) 
              (:background (:current new-screen))))
        (assoc new-screen :next-frame (popup-dialog new-screen))
      (or (nil? (:scenes new-screen))
          (empty? (:scenes new-screen)))
        (start-new-game new-screen)
      :else
        new-screen
    )
  )
)


(defn draw [screen]
  (let [bg (:background (:current screen))
        ctx (:context screen)]
    (when [(not (nil? bg))
      (gimage/draw-image ctx [0 0] bg)])
  )
  screen
)

(defn init [ctx canvas scenes]
  (-> gstate/*base-screen*
    (into 
      {
       :id "Ending"
       :update update
       :render (fn [a b] (draw a))
       :handle-input (fn [a b c] a)
       :next-frame nil
       :context ctx
       :canvas canvas
       :images []
       :deinit (fn [s] nil)
       :bgm :chromatic
       :scenes scenes
       :current { :background :mainmenubg :message ["test"]}
      }
    )
    (gsound/screen-start-bgm)
  )
)

