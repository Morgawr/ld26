(ns ld26.sound
  (:require [goog.dom :as dom])
)


(def *sound-map* (atom {}))

(def *bgm* (atom []))

(declare load-sound)
(defn load-error [uri sym]
  (let [window (dom/getWindow)]
    (. window setTimeout #(load-sound uri sym) 200)
  )
)

(defn load-sound [uri sym]
  (let [sound (js/Audio.)]
    (set! (. sound -loop) true)
    (. sound addEventListener "loadeddata" 
       (fn []
         (doall (swap! *sound-map* assoc sym sound))
       )
    )
    (set! (. sound -onerror) #(load-error uri sym))
    (set! (. sound -src) uri)
  )
)

(defn stop-audio [sound]
  (. sound pause)
)

(defn play-audio [sound]
  (set! (. sound -currentTime) 0)
  (set! (. sound -loop) true)
  (. sound play)
)

(defn play-bgm [sym]
  (let [curr-sym (second @*bgm*)
        sound1 (first @*bgm*)
        sound2 (sym @*sound-map*)]
    (cond
      (nil? curr-sym)
        (do
          (play-audio sound2)
          (reset! *bgm* [sound2 sym])
        )
      (= curr-sym sym) nil
      :else  
       (do
         (stop-audio sound1)
         (play-audio sound2)
         (reset! *bgm* [sound2 sym])
       )
    )
  )
)

(defn screen-start-bgm [screen]
  (play-bgm (:bgm screen))
  screen
)
    
