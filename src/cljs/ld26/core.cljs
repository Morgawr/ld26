(ns ld26.core
  (:require [clojure.browser.repl :as repl]
            [goog.dom :as dom]
            [goog.events :as events]
            [goog.events.EventType :as event-type]
            [ld26.input :as input]
            [ld26.state :as state]
            [ld26.loading])
)

(defn ^:export connect []
;  (.log js/console "Starting local connection...")
;  (repl/connect "http://localhost:9000/repl")
;  (.log js/console "...connected!")
)

(defn ^:export init []
  "This is the init function, it's where everything begins."
  (let [document (dom/getDocument)
        canvas (dom/getElement "surface")
        ctx (.getContext canvas "2d")
        loading (ld26.loading/init ctx canvas)
        state (state/State. [loading] 0 ctx canvas)]
    (events/listen js/window event-type/MOUSEMOVE input/mouse-move-listener)
    (events/listen js/window event-type/CLICK input/mouse-click-listener)
    (state/main-game-loop state)
  )
)
