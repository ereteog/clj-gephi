(ns clj-gephi.layout
  (:require [schema.core :as s])
  (:import [org.gephi.layout.plugin.force StepDisplacement])
  (:import [org.gephi.layout.plugin.force.yifanHu YifanHuLayout])
  )

(s/defschema YifanHuOpts
  {
   :step-displacement s/Num
   (s/optional-key :nb-loops)          s/Num
   (s/optional-key :optimal-distance) s/Num
   })

(defn yifan-hu!
  "GraphModel -> YifanHuOpts -> YifanHuLayout"
  [gm opts]
  (s/validate YifanHuOpts opts)
  (let [{sd       :step-displacement
         opt-dist :optiomal-distance
         nb-loops :nb-loops} opts
        lay (YifanHuLayout. nil (StepDisplacement. sd))]
    (.setGraphModel lay gm)
    (.resetPropertiesValues lay)
    (when opt-dist
      (.setOptimalDistance opt-dist))
    (.initAlgo lay)
    (loop [n nb-loops]
      (when (and (pos? n) (.canAlgo lay))
        (.goAlgo lay)
        (recur (dec n))))
    (.endAlgo lay)
    lay
    ))
