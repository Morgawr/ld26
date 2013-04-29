(ns ld26.mainmenu
  (:require [ld26.image :as gimage]
            [ld26.state :as gstate]
            [ld26.actionscreen :as gascreen]
            [ld26.sound :as gsound])
)



(defn handle-input [screen mouse]
  (let [x (:x mouse)
        y (:y mouse)]
    (if (:clicked mouse)
      (assoc screen :clicked true)
      screen
    )
  )
)

(defn maybe-handle-input [screen on-top mouse]
  (if on-top
    (handle-input screen mouse)
    screen
  )
)

(defn start-game [screen]
  (let [ctx (:context screen)
        canvas (:canvas screen)]
    (assoc screen
           :next-frame
           (fn [state]
             (let [screen-list (:screen-list state)
                   ascreen (gascreen/init ctx canvas [] :1stroom)
                   new-list (gstate/push-screen ascreen screen-list)]
               (assoc state :screen-list new-list)
             )
           )
          :clicked false
    )
  )
)

(defn update [screen on-top elapsed-time]
  (if (:clicked screen)
    (start-game screen)
    screen
  )
)


(defn draw [screen]
  (let [bg (:background screen)
        ctx (:context screen)]
    (gimage/draw-image ctx [0 0] bg)
  )
  screen
)

(defn init [ctx canvas]
  (-> gstate/*base-screen*
    (into {
           :id "MainMenu"
           :update update
           :render (fn [a b] (draw a))
           :handle-input maybe-handle-input
           :next-frame nil
           :background :mainmenubg
           :context ctx
           :canvas canvas
           :images []
           :deinit (fn [s] nil)
           :clicked false
           :bgm :beginning
          }
    )
    (gsound/screen-start-bgm)
  )
)
