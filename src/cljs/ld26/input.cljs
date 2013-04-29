(ns ld26.input)

(def *mouse-state* (atom {:x 0
                          :y 0
                          :clicked false
                          }))

(defn mouse-move-listener [event]
  (swap! *mouse-state* assoc :x (.-clientX event) :y (.-clientY event))
)

(defn mouse-click-listener [event]
  (swap! *mouse-state* assoc :clicked true)
)

(defn mouse-declick []
  (swap! *mouse-state* assoc :clicked false)
)
