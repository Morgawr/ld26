(ns ld26.item
  (:require [ld26.data]
            [ld26.image :as gimage])
)

; This file contains all the related functions for items and generating events
; based on these items.
; It also contains functions operating on the inventory.

(defrecord Item [id ; unique identifier for the item
                 image ; image of this item
                 inventory-image ; image of the item in the inventory
                 position ; coordinates of the item on screen
                 combine-with ; unique identifier of item to combine
                 examine-text ; text to output when identifying 
                 pickup-text ; text to output when picking up
                 generate-events ; functions to call when combined
                 pickupable ; if the item can be picked up
                 hover-text ; text displayed when mouse is on it
                ])

; Functions for events are of the format (fn [screen item1 item2] ... )
; where item1 and item2 are the two combined items

(def *base-item* (Item. "" nil nil [0 0] nil nil nil [] false ""))

; We create exits here because they work pretty much like items
(defrecord Exit [id ; unique identifier for the exit
                 image ; image of the exit
                 position ; position on the screen
                 combine-with ; unique id of an ITEM (not exit!!!)
                 examine-text 
                 generate-events ; usually transitioning events or events 
                                 ; that modify the screen
                 hover-text
                 leads-to ; This is active only when combine-with is nil
                ])

(def *base-exit* (Exit. "" nil [0 0] nil "" [] "" ""))


(defn is-pickupable? [item]
  (:pickupable item)
)

(defn draw-item [item ctx]
  (let [pos (:position item)
        name (:image item)]
    (gimage/draw-image ctx pos name)
  )
)

(defn draw-exit [exit ctx]
  (let [pos (:position exit)
        name (:image exit)]
    (gimage/draw-image ctx pos name)
  )
)

(defn draw-item-inventory [item ctx]
  (let [pos (:position item)
        name (:inventory-image item)]
    (gimage/draw-image ctx pos name)
  )
)

(defn create-item [id]
  (-> *base-item*
    (into (id ld26.data/*item-list*))
    (#(assoc % :id id))
  )
)

(defn create-exit [id]
  (-> *base-exit*
    (into (id ld26.data/*exit-list*))
    (#(assoc % :id id))
  )
)

(defn pick-up [inventory item]
  (if (is-pickupable? item)
    (conj inventory item)
    nil
  )
)
