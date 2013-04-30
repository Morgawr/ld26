(ns ld26.loading
  (:require [ld26.image :as gimage]
            [ld26.state :as gstate]
            [ld26.sound :as gsound]
            [ld26.mainmenu])
)

; This is the loading screen, it is the first screen that we load in the game
; and its task is to load all the resources (images, sounds, etc etc) of the
; game before we can begin playing.

(def image-list 
  {
   :mainmenubg "img/mainmenubg.png"
   :dialoggui "img/dialogbg.png"
   :testbg "img/testbg.png"
   :inventorybase "img/inventorybase.png"
   :flower "img/flower.png"
   :flower-pocket "img/flower-pocket.png"
   :combine-norm "img/combine-norm.png"
   :combine-sel "img/combine-sel.png"
   :take-norm "img/take-norm.png"
   :take-sel "img/take-sel.png"
   :examine-norm "img/examine-norm.png"
   :examine-sel "img/examine-sel.png"
   :selected-item "img/selected-item.png"
   :duck "img/duck.png"
   :duck-pocket "img/duck-pocket.png"
   :pretty-duck "img/pretty-duck.png"
   :up-arr "img/up-arrow.png"
   :down-arr "img/down-arrow.png"
   :base-door "img/base-door2.png"
   :background1 "img/bg1.png"
   :portal-1 "img/portal-1.png"
   :window-1 "img/window-1.png"
   :tree-1 "img/tree-1.png"
   :mushroom "img/mushroom-ground.png"
   :mushroom-pocket "img/mushroom-pocket.png"
   :passage-1 "img/passage-1.png"
   :waterfall "img/waterfall.png"
   :mushroom-big "img/mushroom-big.png"
   :table "img/table.png"
   :fruit-basket "img/fruit-basket.png"
   :fruit-pocket "img/fruit-pocket.png"
   :girl-sign "img/girl-sign.png"
   :girl-sign-pocket "img/girl-sign-pocket.png"
   :man-sign "img/man-sign.png"
   :man-sign-pocket "img/man-sign-pocket.png"
   :background2 "img/bg2.png"
   :fisherman "img/fisherman.png"
   :girl "img/girl.png"
   :back-arrow "img/back-arrow.png"
   :castle-room1 "img/castle-room1.png"
   :princess-door "img/princess-door.png"
   :forest "img/forest.png"
   :far-castle "img/far-castle.png"
   :waterfall-bg "img/waterfall-room.png"
   :cave-water-bg "img/cave-waterfall.png"
   :bottle "img/bottle.png"
   :castle-bg "img/castle-hall.png"
   :polo "img/polo.png"
   :lolo "img/lolo.png"
   :throne-door "img/throne-door.png"
   :side-door "img/side-door.png"
   :castle-corridor "img/castle-corridor.png"
   :library-door "img/library-door.png"
   :bedroom-door "img/bedroom-door.png"
   :gate "img/gate.png"
   :library "img/library.png"
   :bookcase "img/bookcase.png"
   :note "img/note.png"
   :note-pocket "img/note-pocket.png"
   :bed "img/bed.png"
   :bedroom "img/bedroom.png"
   :half-trinket1 "img/half-trinket1.png"
   :half-trinket2 "img/half-trinket2.png"
   :full-trinket "img/full-trinket.png"
   :throne-room "img/throne-room.png"
   :throne "img/throne.png"
   :transition-1 "img/transition-1.png"
   :transition-2 "img/transition-2.png"
   :transition-3 "img/black.png"
   :transition-4 "img/transition-4.png"
  }
)

(def audio-list
  {
   :beginning "sound/beginning.ogg"
   :chromatic "sound/chromatic-simplicity.ogg"
   :jumping "sound/jumping-rocks.ogg"
   :wavy "sound/wavy-curves.ogg"
  }
)

(defn handle-input [screen mouse]
  (if (and (:complete screen) (:clicked mouse) (not (:advance screen)))
    (assoc screen :advance true)
    screen
  )
)

(defn maybe-handle-input [screen on-top mouse]
  (if on-top
    (handle-input screen mouse)
    screen
  )
)

(defn load-main-menu [screen]
  (let [ctx (:context screen)
        canvas (:canvas screen)]
    (assoc screen 
           :next-frame 
           (fn [state]
             (let [screen-list (:screen-list state)
                   mmenu (ld26.mainmenu/init ctx canvas)
                   new-list (gstate/replace-screen mmenu screen-list)]
               (assoc state :screen-list new-list)
              )
            )
    )
  )
)

(defn percentage-loaded [imgcount sndcount]
  (let [max (+ (count @gimage/*image-map*)
               (count @gsound/*sound-map*))
        ptg (* (/ max (+ imgcount sndcount)) 100)]
    (int ptg)
  )
)

(defn everything-loaded [screen]
  (let [complete true
        message "Finished loading, click to continue"]
    (assoc screen :complete complete :message message 
           :percentage (percentage-loaded 
                         (count image-list) (count audio-list)))
  )
)

(defn has-loaded? [num res-map]
  (= num (count res-map))
)

(defn all-loaded? [imgcount sndcount]
  (and (has-loaded? imgcount @gimage/*image-map*)
       (has-loaded? sndcount @gsound/*sound-map*))
)

(defn load-sounds [screen]
  (doseq [[k v] audio-list] (gsound/load-sound v k))
  (assoc screen :loading-status 1)
)

(defn update [screen elapsed-time]
  (let [images (count image-list)
        sounds (count audio-list)]
    (cond
      (:advance screen) (load-main-menu screen)
      (:complete screen) screen
      (and (zero? (:loading-status screen))
           (has-loaded? images
                        @gimage/*image-map*))
        (load-sounds screen)
      (all-loaded? images sounds) (everything-loaded screen)
      :else (assoc screen :percentage (percentage-loaded images sounds))
    )
  )
)

(defn maybe-update [screen on-top elapsed-time]
  (if on-top
    (update screen elapsed-time)
    screen
  )
)

(defn draw [screen]
  (gimage/draw-text-centered (:context screen) 
                             [400 250] 
                             (:message screen)
                             "25px"
                             "white")
  (gimage/draw-text-centered (:context screen)
                             [400 300]
                             (str (:percentage screen) "%")
                             "25px"
                             "white")
  screen
)

(defn maybe-draw [screen on-top]
  (if on-top
    (draw screen)
    screen
  )
)

(defn init [ctx canvas]
  (doseq [[k v] image-list] (gimage/load-image v k))
  ;(doseq [[k v] audio-list] (gsound/load-sound v k))
  (-> gstate/*base-screen*
    (into {
           :id "LoadingScreen"
           :update maybe-update
           :render maybe-draw
           :handle-input maybe-handle-input 
           :next-frame nil
           :context ctx
           :canvas canvas
           :images []
           :deinit (fn [s] nil)
           :advance false
           :complete false
           :message "Loading..."
           :percentage 0
           :loading-status 0 ; 0 = image, 1 = sound
          }
    )
  )
)
