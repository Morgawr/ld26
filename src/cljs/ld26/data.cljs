(ns ld26.data)

; This file only contains the data for the game, scripts, events, screens,
; items, all related to the game

(def *text-data*
  {
   :flower-examine-text [ "It's a flower" ]
   :flower-pickup-text [ "I just picked up a flower" ]
   :duck-examine-text [ "It's a duck"
                        "quack" ]
   :duck-pickup-text [ "I have a duck"
                       "quack" ]
   :pretty-duck-examine-text [ "This duck is pretty, now!" ]
   :test-chamber-speech []
   :intro-speech [ "You slowly wake up from your slumber, you feel dizzy and confused."
                   "\"Where... where am I?\""
                   "\"Oh, right... I was asleep. How long did I sleep? It feels like I've been dreaming for ages.\""
                   "\"This world... It's so strange. So devoid of emotions and colors. I need to break out of here. I need to find a way back home to my own world.\"" ]
   :valley-speech
     [ "A castle stands to your right, in the distance. A thick forest lies on the left of a river, where an old fisherman is gently relaxing beneath the sun."
       "The old man looks at you for a moment, then nods as a greeting."]
   :fisherman-speech
     [ "\"Good morning young lad, what a beautiful day, isn't it?\""
       "He sighs. \"If only my luck were a bit better, I can't catch a single fish now that I lost my lucky brooch.\"" ]
   :castle1-speech
     [ "You slowly make your way into the castle, following its corridors and niches until you get to a room with a table full of food."
       "A young girl stands in front of the table and looks at you with a serious expression."
       "\"I don't really think you should be here\" she says." ]
   :cave-water-speech 
     [ "The cave behind the waterfall doesn't seem to lead anywhere."
       "\"What an eerie place, who would've thought... Wait, what's that?\""
      "You see something shine in the distance, illuminated by a ray of light coming from the ceiling of the cave." ]
   :fisherman-thanks
     [ "\"Oh my?! If it isn't my lucky brooch. Where did you find it, lad?\""
       "He looks up at you with a smiling face, you shrug and nod towards the forest."
       "\"A hidden passage behind the waterfall? Haha, this is surely worthy of some legend.\""
       "\"Here here, have a sip of this as a thank you, I'm sure you'll like it.\""
       "He stands up, gathers his things and starts toward the forest, laughing wholeheartedly." ]
   :castle2-speech
     [ "You enter the main castle hall, two guards stand in front of the throne room archway, blocking your way."
       "There is a small door at the left side of the room, leading deeper into the castle."]
   :library-speech
     [ "A gigantic bookcase stands in front of you, you have found the castle library!"
       "The architect must have been drunk, the bookcase itself seems to be designed by a five years old kid."
       "(Author's note: \"It was on purpose guys, I swear.\")" ]
   :bedroom-speech
    [ "A queen-sized bed stands in front of you, this must be the bedroom of somebody very important."
      "There seems to be a signet of some sort on top of the bed."
      "\"hmm... I wonder what it's going to do on it.\"" ]
   :throne-speech
    [ "At last, you reach the end of your journey. You sneakily enter the throne room, trying not to avoid too many eyes."
      "As you advance inside, you soon realize there is nobody in here. No king nor queen. Peculiar. " ]
  }
)

(def *event-data*
  {
   :flower-combined (fn [inventory callback] 
                      (filter #(not (= (:id %) :flower)) inventory)
                    )
   :duck-combined   (fn [inventory callback] 
                      (callback (filter #(not (= (:id %) :duck)) inventory)
                                :pretty-duck)
                    )
   :default-exit (fn [screen callback exit]
                   (callback screen (:leads-to exit) nil)
                 )
   :princess-got-signet-remove (fn [items callback]
                                 (callback 
                                   (filter #(not (= (:id %) :princess1))
                                           items)
                                   :princess2
                                 )
                               )
   :mushroom-used (fn [inventory callback]
                    (callback (filter #(not (= (:id %) :mushroom)) inventory)
                              nil)
                  )
   :waterfall-parted (fn [items callback]
                       (callback (filter #(not (= (:id %) :waterfall))
                                         items)
                                 :mushroom-big
                       )
                     )
   :man-got-signet-remove (fn [items callback]
                            (callback (filter #(not (= (:id %) :fisherman))
                                              items)
                                      nil
                            )
                          )
   :man-get-bottle (fn [inventory callback]
                     (callback (filter #(not (= (:id %) :man-signet))
                                       inventory)
                               :bottle
                     )
                   )
   :obtain-weird-fruit (fn [inventory callback]
                         (callback (filter #(not (= (:id %) :girl-signet))
                                           inventory)
                                   :weird-fruit
                         )
                       )
   :obtain-trinket1 (fn [inventory callback]
                      (callback (filter #(not (= (:id %) :bottle))
                                        inventory)
                                :half-trinket1
                      )
                    )
   :obtain-trinket2 (fn [inventory callback]
                      (callback (filter #(not (= (:id %) :weird-fruit))
                                        inventory)
                                :half-trinket2
                      )
                    )
   :lolo-leaves (fn [items callback]
                  (callback (filter #(not (= (:id %) :lolo))
                                    items) nil)
                )
   :polo-leaves (fn [items callback]
                  (callback (filter #(not (= (:id %) :polo))
                                    items) nil)
                )
   :remove-trinket-1 (fn [inventory callback]
                       (callback (filter #(not (= (:id %) :half-trinket1))
                                         inventory) nil)
                     )
   :remove-trinket-2 (fn [inventory callback]
                       (callback (filter #(not (= (:id %) :half-trinket2))
                                         inventory) :full-trinket)
                     )
   :remove-full-trinket (fn [inventory callback]
                          (callback (filter #(not (= (:id %) :full-trinket))
                                            inventory) nil)
                        )
   :remove-throne (fn [items callback]
                    (callback (filter #(not (= (:id %) :throne))
                                      items) nil)
                  )
   :final-exit (fn [screen callback exit]
                 (callback screen nil :ending-screen)
               )
  }
)

(def *item-list* 
  {
   :mushroom {
              :image :mushroom
              :inventory-image :mushroom-pocket
              :combine-with :waterfall
              :examine-text [ "A very dried mushroom. It looks poisonous." ]
              :generate-events
               [ (:mushroom-used *event-data*) ]
              :pickupable true
              :hover-text "Mushroom"
             }
   :mushroom-big {
                  :image :mushroom-big
                  :examine-text [ "All that was needed was some water and look at how big this little guy has grown."
                                  "It broke the flow of the waterfall, there seems to be a hidden passage behind. "]
                  :hover-text "Grown Mushroom"
                  :position [180 -150]
                 }
   :fisherman {
               :image :fisherman
               :combine-with :man-signet
               :examine-text ( :fisherman-speech *text-data* )
               :generate-events
                [ (:man-got-signet-remove *event-data*) ]
               :hover-text "Old Fisherman"
              }
   :table1 {
            :image :table
            :examine-text ["A table full of food."]
            :hover-text "Table"
           }
   :window1 {
             :image :window-1
             :examine-text [ "A heavily decorated window."
                             "You can see an inner garden on the other side."]
             :hover-text "Window"
            }
   :princess1 {
               :image :girl
               :examine-text [ "\"What do you want?\" she asks with a loudly pitched voice."
                               "\"If you keep staring at me like this I'm going to call the guards! You creep!\""
                               "She does not look friendly."]
               :generate-events [ (:princess-got-signet-remove *event-data*)]
               :combine-with :girl-signet
               :hover-text "Princess"
             }
   :girl-signet {
                 :image :girl-sign
                 :inventory-image :girl-sign-pocket
                 :combine-with :princess1
                 :examine-text [ "Seems to be a signet, or something similar to that."]
                 :generate-events [ (:obtain-weird-fruit *event-data*)]
                 :pickup-text [ "\"What? This thing changed when I picked it up...\""]
                 :pickupable true
                 :hover-text "Signet"
                }
   :fruit-basket {
                  :image :fruit-basket
                  :examine-text [ "As you lower your eyes toward the basket full of weird fruit, the girl turns to you."
                                 "\"What do you think you're doing?\""
                                 "\"Don't you dare touch this food, it's mine!\"" ]
                  :hover-text "Fruit"
                 }
   :weird-fruit {
                 :inventory-image :fruit-pocket
                 :examine-text [ "A weird fruit, it does not look tasty at all." ]
                 :pickup-text [ "\"Ahh! It's my family sigil!\" The girl exclaims in surprise."
                                "\"I thought I had lost it, where did you find it?\""
                                "\"Nah, nevermind, don't tell me.\" She shrugs nonchalantly. \"Here, you can have this as a reward, it's Polo's favorite food, tee hee.\""
                                "She gives you some weird-looking fruit."]
                 :hover-text "Weird fruit"
                 :combine-with :polo
                 :generate-events
                  [(:obtain-trinket2 *event-data*)]
                }
   :princess2 {
               :image :girl
               :examine-text [ "\"Thank you for finding this, I had lost it a few days ago.\""
                               "\"I already gave you one fruit, now please leave before I call the guards.\""]
               :hover-text "Princess"
               :position [250 60]
             }
   :waterfall {
               :image :waterfall
               :examine-text [ "A gentle and soothing waterfall." ]
               :hover-text "Waterfall"
               :generate-events 
                 [ (:waterfall-parted *event-data* ) ]
               :combine-with :mushroom
              }
   :man-signet {
                :image :man-sign
                :inventory-image :man-sign-pocket
                :examine-text [ "Looks like a brooch of some sort." ]
                :pickup-text [ "\"Uh..? What? The brooch looks different now.\"" ]
                :hover-text "Man's Signet"
                :generate-events 
                  [(:man-get-bottle *event-data* )]
                :combine-with :fisherman
                :pickupable true
               }
   :bottle {
            :inventory-image :bottle
            :examine-text [ "Seems to be a bottle containing some sort of liquor."
                            "It's very strong." ]
            :hover-text "Bottle of Liquor"
            :generate-events [(:obtain-trinket1 *event-data*)] 
            :pickupable true
            :pickup-text (:fisherman-thanks *text-data*)
            :combine-with :lolo
           }
   :note {
          :image :note
          :inventory-image :note-pocket
          :examine-text [ "On the note it says that Lolo the guard was found drinking during his shift again."
                          "Apparently now he's banned from the castle cellars, they even had to build a custom gate to stop him." ]
          :hover-text "Note"
          :pickupable true
          :pickup-text [ "It seems to be a list of the latest rumors going on in the castle, a maid must have dropped it." ]
         }
   :polo {
          :image :polo
          :examine-text [ "\"...\"" 
                          "The guard glares at you with a stern expression."
                          "He won't budge."]
          :hover-text "Polo"
          :generate-events [ (:polo-leaves *event-data*)] 
          :combine-with :weird-fruit
         }
   :lolo {
          :image :lolo
          :examine-text [ "\"...\"" 
                          "The guard glares at you with a stern expression."
                          "He won't budge."]
          :hover-text "Lolo"
          :generate-events [ (:lolo-leaves *event-data*)] 
          :combine-with :bottle
         }
   :gate {
          :image :gate
          :examine-text [ "An immovable gate. It's locked." ]
          :hover-text "Locked Gate"
         }
   :bookcase {
              :image :bookcase
              :examine-text [ "You take a look at the various book labels until... \"Ah ha!\""
                              "Your eyes fall on a specific book titled \"The Throne Room\"."
                              "\"Interesting... the book seems to talk about parallel worlds and how the throne room supposedly holds the secret to reach these worlds.\""
                              "\"I must find a way to sneak into the throne room!\"" 
                              "You gently put the book back in the bookcase." ]
              :hover-text "Bookcase"
             }
   :bed {
         :image :bed
         :examine-text [ "It's a pretty big bed, wah!" ]
         :hover-text "Queen-sized bed"
        } 
   :half-trinket1 {
                   :inventory-image :half-trinket1
                   :examine-text [ "A peculiar trinket, it points up." ]
                   :hover-text "Trinket Piece"
                   :pickup-text [ "\"Hold it, boy! Where did you get that spirit? It's my favorite!\""
                                  "You show him the bottle of liquor. \"Can I have it?\""
                                  "You nod silently. He steals the bottle right from your hands and gulps it in one, long sip."
                                 "\"Ahh.. *hic* I'm feeling much... *hic* ...better now\" he says, drawling his words."
                                 "\"Curse this job, I'm outta here!\" He starts walking towards the exit of the castle, you notice he dropped something. It looks like a small trinket, but it seems broken." ]
                   :pickupable true
                   :combine-with :half-trinket2
                   :generate-events [ (:remove-trinket-1 *event-data*) ]
                  }
   :half-trinket2 {
                   :inventory-image :half-trinket2
                   :examine-text [ "A peculiar trinket, it points down." ]
                   :hover-text "Trinket Piece"
                   :pickup-text [ "You show Polo the weird-looking fruit, his eyes seem to light up."
                                  "\"F-f-for me? W-w-wow... Thank you!\" He stutters."
                                  "He takes the fruit from you before you can even say anything and he starts peeling it."
                                  "\"H-h-h-here, t-t-take t-t-this as a r-r-r-reward.\" He offers you some shiny trinket."
                                  "As you put the trinket in your pockets he starts walking towards the kitchen, leaving the castle hall." ]
                   :pickupable true
                   :combine-with :half-trinket1
                   :generate-events [ (:remove-trinket-2 *event-data*) ]
                  }
   :full-trinket {
                  :inventory-image :full-trinket
                  :examine-text [ "A glowing sigil, it does not look natural." ]
                  :hover-text "Glowing Trinket"
                  :generate-events [ (:remove-full-trinket *event-data*)]
                  :combine-with :throne
                  :pickup-text [ "The two sides suddenly attract each other and snap together, creating a single, glowing trinket."
                                "It emanates a very soothing and warm feeling." ]
                 }
   :throne {
            :image :throne
            :hover-text "Throne"
            :examine-text [ "A majestic throne stands in front of you. Its owner seems to be missing."
                            "Right in the middle of the seat there is an empty slot." ]
            :generate-events 
             [ (:remove-throne *event-data* )]
            :combine-with :full-trinket
           }
  }
)

; Exits without a :combine-with element are passthrough exits, this means that
; they operate on events of the format (screen event exit) -> (screen id stype)
; if stype is nil then the exit leads to another action screen (identified by
; the id), else it requires a specific (cond ) statement to operate on
; initialization.
; The other type of exit operates on events of the format (exit callback) ->
; (exits id)
(def *exit-list*
  {
   :door1 {
           :image :base-door
           :examine-text [ "\"Am I ready to face the new world?\"" ]
           :generate-events
             [ (:final-exit *event-data*) ]
           :hover-text "Artsy Portal"
           :leads-to nil
          }
   :portal1 {
             :image :portal-1
             :examine-text [ "\"This passage leads outside.\"" ]
             :generate-events
              [ (:default-exit *event-data*) ]
             :hover-text "Passage"
             :leads-to :valley
            }
   :back1 {
           :image :back-arrow
           :leads-to :1stroom
           :examine-text [ "\"Leads to the cave where I started.\""]
           :hover-text "Back"
           :generate-events
            [ (:default-exit *event-data*) ]
          }
   :back2 {
           :image :back-arrow
           :leads-to :valley
           :examine-text [ "The way back to the valley." ]
           :hover-text "Back"
           :generate-events
            [ (:default-exit *event-data*) ]
          }
   :back3 {
           :image :back-arrow
           :leads-to :waterfall-room
           :examine-text [ "The passage back to the big mushroom." ]
           :hover-text "Back"
           :generate-events
            [ (:default-exit *event-data*) ]
          }
   :back4 {
           :image :back-arrow
           :leads-to :castle1
           :examine-text [ "To the dining room." ]
           :hover-text "Back"
           :generate-events
            [ (:default-exit *event-data*) ]
          }
   :back5 {
           :image :back-arrow
           :leads-to :castle2
           :examine-text [ "To the castle hall." ]
           :hover-text "Back"
           :generate-events
            [ (:default-exit *event-data*) ]
          }
   :back6 {
           :image :back-arrow
           :leads-to :corridor
           :examine-text [ "To the corridor." ]
           :hover-text "Back"
           :generate-events
            [ (:default-exit *event-data*) ]
          }
   :forest {
            :image :forest
            :leads-to :waterfall-room
            :examine-text [ "A forest in the distance." ]
            :hover-text "Forest"
            :generate-events
             [ (:default-exit *event-data*) ]
           }
   :forest-cave {
                 :image :passage-1
                 :leads-to :cave-waterfall
                 :examine-text [ "There seems to be a passage behind the waterfall."]
                 :hover-text "Hidden Passage"
                 :generate-events
                  [ (:default-exit *event-data*) ]
                }
   :castle-entrance {
                     :image :far-castle
                     :leads-to :castle1
                     :examine-text [ "A massive castle."
                                     "\"There must be a library or somebody who can tell me how to get out of here!\"" ]
                     :hover-text "Castle"
                     :generate-events
                       [ (:default-exit *event-data* )] 
                    }
   :princess-door {
                   :image :princess-door
                   :leads-to :castle2
                   :examine-text [ "It's a very gilded and prestigious door." ]
                   :hover-text "To the Hall"
                   :generate-events
                     [ (:default-exit *event-data* ) ]
                  }
   :throne-door {
                 :image :throne-door
                 :leads-to :throne-room
                 :examine-text [ "It's a majestic arch, it must lead to the throne room."]
                 :hover-text "Throne Room"
                 :generate-events
                   [ (:default-exit *event-data* ) ]
                }
   :side-door {
                 :image :side-door
                 :leads-to :corridor
                 :examine-text [ "A small service door."]
                 :hover-text "Service Door"
                 :generate-events
                   [ (:default-exit *event-data* ) ]
                }
   :bedroom-door {
                 :image :bedroom-door
                 :leads-to :bedroom
                 :examine-text [ "A small and anonymous door."]
                 :hover-text "To the bedroom"
                 :generate-events
                   [ (:default-exit *event-data* ) ]
                }
   :library-door {
                 :image :library-door
                 :leads-to :library
                 :examine-text [ "A really insignificant door."]
                 :hover-text "To the library"
                 :generate-events
                   [ (:default-exit *event-data* ) ]
                }
  }
)

(def *action-screens*
  {
   :testchamber {
                 :items [ { :id :flower :x 60 :y 320 } 
                          { :id :duck :x 500 :y 300 } ]
                 :background :testbg
                 :exits [ { :id :door1 :x 300 :y 50 } ]
                 :first-speech (:test-chamber-speech *text-data*)
                 :bgm :jumping
                }
   :valley {
            :items [ { :id :fisherman :x 250 :y 110 } ]
            :background :background2
            :exits [ { :id :back1 :x 720 :y 220 } 
                     { :id :forest :x 40 :y 110 }
                     { :id :castle-entrance :x 550 :y -20 } ]
            :first-speech ( :valley-speech *text-data* )
            :bgm :wavy
           }
   :1stroom {
             :items []
             :background :background1
             :exits [ {:id :portal1 :x 250 :y 110 } ]
             :first-speech ( :intro-speech *text-data* )
             :bgm :wavy
            }
   :castle1 {
             :items [ { :id :fruit-basket :x 105 :y 220 }
                      { :id :table1 :x 30 :y 300 } 
                      { :id :window1 :x 25 :y 50 } 
                      { :id :princess1 :x 250 :y 60 } ]
             :background :castle-room1
             :exits [ {:id :back2 :x 720 :y 220 }
                      {:id :princess-door :x 455 :y 95 } ]
             :first-speech ( :castle1-speech *text-data* )
             :bgm :wavy
            }
   :castle2 {
             :items [ { :id :polo :x 235 :y 30 }
                      { :id :lolo :x 435 :y 30 } ]
             :background :castle-bg
             :exits [ { :id :back4 :x 720 :y 220 }
                      { :id :throne-door :x 350 :y 45 } 
                      { :id :side-door :x 70 :y 160 } ]
             :first-speech ( :castle2-speech *text-data* )
             :bgm :jumping
            }
   :waterfall-room {
                    :items [ { :id :waterfall :x 160 :y -50 } 
                             { :id :mushroom :x 20 :y 350 }]
                    :background :waterfall-bg
                    :exits [{:id :forest-cave :x 240 :y 0 } 
                            {:id :back2 :x 720 :y 220} ]
                    :first-speech [ "You follow the bubbling sound of the river through the forest and reach a very calm and soothing valley."
                                   "A waterfall is pouring gently from a rock, up high above you." ]
                    :bgm :wavy
                   }
   :cave-waterfall {
                    :items [ { :id :man-signet :x 340 :y 170 } ]
                    :background :cave-water-bg
                    :exits [{:id :back3 :x 720 :y 220}]
                    :first-speech ( :cave-water-speech *text-data* )
                    :bgm :wavy
                   }
   :corridor {
              :items [ {:id :gate :x 310 :y 80} ]
              :exits [ {:id :back5 :x 720 :y 220 }
                       {:id :bedroom-door :x 480 :y 180 }
                       {:id :library-door :x 155 :y 160}  ]
              :background :castle-corridor
              :bgm :jumping
             }
   :library {
             :items [ {:id :bookcase :x 130 :y 55}
                      {:id :note :x 40 :y 360 } ]
             :exits [ {:id :back6 :x 720 :y 220 }]
             :first-speech ( :library-speech *text-data* )
             :background :library
             :bgm :jumping
            }
   :bedroom {
             :items [ {:id :girl-signet :x 300 :y 220 }
                      {:id :bed :x 50 :y 100}]
             :exits [ {:id :back6 :x 720 :y 220 } ]
             :first-speech ( :bedroom-speech *text-data* )
             :background :bedroom
             :bgm :jumping
            }
   :throne-room {
                 :items [ {:id :throne :x 320 :y 70 } ]
                 :exits [ {:id :door1 :x 370 :y 80 } 
                          {:id :back5 :x 720 :y 220 } ]
                 :first-speech ( :throne-speech *text-data* )
                 :background :throne-room
                 :bgm :jumping
                }
  }
)


(def final-scene [
                  { 
                   :background :transition-1
                   :messages ["You gently walk towards the light, through the portal behind the throne room."
                              "You are afraid, it's the first time you've taken such an important step. You don't seem to recall anymore why you were in this world and how it happened."
                              "It just didn't matter, all you wanted was to escape. This weird world, its weird inhabitants..."
                              "It wasn't for you, you didn't need it. You didn't want it."
                              "And now it's over."]
                  }
                  {
                   :background :transition-2
                   :messages ["All I remember, a huge crash."
                              "Lights, from a car, approaching me at a tremendous speed."
                              "I can't. I can't move. I'm frozen."
                              "I see him, I see the shadow of the driver through the windshield..."
                              "...and I know that will be the last thing I will ever see."]
                  }
                  {
                   :background :transition-3
                   :messages [ "Alone, in the darkness. Looking for the light."
                               "That very same light that killed me on that day."]
                  }
                  {
                   :background :transition-4
                   :messages [ "\"I... I'm sorry, I didn't see him! I swear!\""
                               "Voices... in the distance I hear voices..."
                               "\"It's alright, it's not your fault...\""
                               "I can hear a woman crying. A voice I should remember."
                               "And yet I don't. I am too tired to remember."
                               "Too tired to sleep."
                               "Which is funny, because I think I had one hell of a dream."
                               "Or was it another life? Yet again something that I should remember, but I forgot."
                               "And now it's time to go. I don't belong here anymore."
                               "I am tired."
                               "Too..."
                               "...tired."]
                  }
                  {
                   :background :transition-3
                   :messages ["I was an artist, once. Before dying."
                              "An accident took my life, I spent weeks in a life-threatening coma, and now I am gone."
                              "During that period, I didn't exit. I just dreamed."
                              "This is how I like to imagine it when we die. Just a dream, a big, funny dream."
                              "Sometimes weird, sometimes scary. Just a dream."
                              "And when it's time to leave, we leave content. We leave happy. Because after all..."
                              "...we wake from the dream."
                            ]
                  }
                 ]
)
