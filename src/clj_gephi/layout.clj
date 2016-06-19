(ns clj-gephi.layout
  (:require [schema.core :as s])
  (:import [org.gephi.layout.plugin.force StepDisplacement])
  (:import [org.gephi.layout.plugin.force.yifanHu YifanHuLayout])
  (:import [org.gephi.layout.plugin.forceAtlas ForceAtlasLayout])
  (:import [org.gephi.layout.plugin AutoLayout])
  (:import [java.util.concurrent TimeUnit])
  (:import [org.gephi.layout.plugin.labelAdjust LabelAdjust])
  )

(s/defschema YifanHuOpts
  {
   :step-displacement                       s/Num
   (s/optional-key :adaptive-cooling?)      s/Bool
   (s/optional-key :barnes-hut-theta)       s/Num
   (s/optional-key :convergence-threshold)  s/Num
   (s/optional-key :initial-step)           s/Num
   (s/optional-key :optimal-distance)       s/Num
   (s/optional-key :quad-tree-max-level)    s/Num
   (s/optional-key :relative-strength)      s/Num                                                  (s/optional-key :step)                   s/Num
   (s/optional-key :step-ratio)             s/Num
   })

(s/defschema ForceAtlasOpts
  {
   (s/optional-key :adjust-sizes?)                     s/Bool
   (s/optional-key :attraction-strength)               s/Num
   (s/optional-key :cooling)                           s/Num
   (s/optional-key :freeze-balance?)                   s/Bool
   (s/optional-key :freeze-inertia)                    s/Num
   (s/optional-key :freeze-strength)                   s/Num
   (s/optional-key :gravity)                           s/Num
   (s/optional-key :inertia)                           s/Num
   (s/optional-key :max-displacement)                  s/Num
   (s/optional-key :outbound-attraction-distribution)  s/Bool
   (s/optional-key :repulsion-strength)                s/Num
   (s/optional-key :speed)                             s/Num
   })

(s/defschema LabelAdjustOpts
  {
   (s/optional-key :adjust-by-size?)                   s/Bool
   (s/optional-key :speed)                             s/Num
   })

(defn update-yifan-hu!
  "YifanHuOpts -> YifanHuLayout"
  [lay opts]
  (s/validate YifanHuOpts opts)
  (let [{adaptive-cooling?         :adaptive-cooling?
         barnes-hut-theta          :barnes-hut-theta
         convergence-threshold     :convergence-threshold
         initial-step              :initial-step
         optimal-distance          :optiomal-distance
         quad-tree-max-level       :quad-tree-max-level
         relative-strength         :relative-strength
         step                      :step
         step-ratio                :step-ratio} opts]
    (when adaptive-cooling?
      (.setAdaptiveCooling lay adaptive-cooling?))
    (when barnes-hut-theta
      (.setBarnesHutTheta lay barnes-hut-theta))
    (when convergence-threshold
      (.setConvergenceThreshold lay convergence-threshold))
    (when initial-step
      (.setInitialStep lay initial-step))
    (when optimal-distance
      (.setOptimalDistance optimal-distance))
    (when quad-tree-max-level
      (.setQuadTreeMaxLevel lay quad-tree-max-level))
    (when relative-strength
      (.setRelativeStrength lay relative-strength))
    (when step
      (.setStep lay step))
    (when step-ratio
      (.setStepRatio lay step-ratio))
    lay))

(defn yifan-hu
  "YifanHuOpts -> YifanHuLayout"
  [opts]
  (let [{sd :step-displacement} opts
        lay (YifanHuLayout. nil (StepDisplacement. sd))]
    (.resetPropertiesValues lay)
    (update-yifan-hu! lay opts)))

(defn update-force-atlas!
  "ForceAtlasLayout -> ForceAtlasOpts -> ForceAtlasLayout"
  [lay opts]
  (s/validate ForceAtlasOpts opts)
  (let [{adjust-sizes?                     :adjust-sizes?
         attraction-strength               :attraction-strength
         cooling                           :cooling
         freeze-balance?                   :freeze-balance?
         freeze-inertia                    :freeze-inertia
         freeze-strength                   :freeze-strength
         gravity                           :gravity
         inertia                           :inertia
         max-displacement                  :max-displacement
         outbound-attraction-distribution  :outbound-attraction-distribution
         repulsion-strength                :repulsion-strength
         speed                             :speed} opts]
    (when adjust-sizes?
      (.setAdjustSizes lay adjust-sizes?))
    (when attraction-strength
    	(.setAttractionStrength lay attraction-strength))
    (when cooling
    	(.setCooling lay cooling))
    (when freeze-balance?
    	(.setFreezeBalance lay freeze-balance?))
    (when freeze-inertia
    	(.setFreezeInertia lay freeze-inertia))
    (when freeze-strength
    	(.setFreezeStrength lay freeze-strength))
    (when gravity
      (.setGravity lay gravity))
    (when inertia
    	(.setInertia lay inertia))
    (when max-displacement
    	(.setMaxDisplacement lay max-displacement))
    (when outbound-attraction-distribution
    	(.setOutboundAttractionDistribution lay outbound-attraction-distribution))
    (when repulsion-strength
    	(.setRepulsionStrength lay repulsion-strength))
    (when speed
    	(.setSpeed lay speed))
    lay))

(defn force-atlas
  "ForceAtlasOpts -> ForceAtlasLayout"
  [opts]
  (let [lay (ForceAtlasLayout. nil)]
    (.resetPropertiesValues lay)
    (update-force-atlas! lay opts)
    lay))

(defn update-label-adjust!
  "LabelAdjust -> LabelAdjustOpts -> LabelAdjust"
  [lay opts]
  (s/validate LabelAdjustOpts opts)
  (let [{adjust-by-size?  :adjust-by-size?
         speed            :speed} opts]
    (when adjust-by-size?
    	(.setAdjustBySize lay adjust-by-size?))
    (when speed
    	(.setSpeed lay speed))
    lay))

(defn label-adjust
  "LabelAdjustOpts -> LabelAdjust"
  [opts]
  (let [lay (LabelAdjust. nil)]
    (.resetPropertiesValues lay)
    (update-label-adjust! lay opts)
    lay))

(defn layout!
  "GraphModel -> AbstractLayout -> AbstractLayout"
  [gm nb-loops lay]
  (.setGraphModel lay gm)
  (.initAlgo lay)
  (loop [n nb-loops]
    (when (and (pos? n) (.canAlgo lay))
      (.goAlgo lay)
      (recur (dec n))))
  (.endAlgo lay)
  lay)

(defn yifan-hu!
  "GraphModel -> YifanHuOpts -> YifanHuLayout"
  [gm nb-loops opts]
  (->> (yifan-hu opts)
       (layout! gm nb-loops)))

(defn force-atlas!
  "GraphModel -> ForceAtlasOpts -> ForceAtlasLayout"
  [gm nb-loops opts]
  (->> (force-atlas opts)
       (layout! gm nb-loops)))

(defn label-adjust!
  "GraphModel -> LabelAdjust"
  [gm nb-loops opts]
  (->> (label-adjust opts)
       (layout! gm nb-loops)))

(defn auto-layout
  "Long -> [[AbastractLayout Double]] -> AutoLayout"
  [milliseconds layouts-ratio]
  (let [lay (AutoLayout. milliseconds TimeUnit/MILLISECONDS)]
    (doseq [[a-lay ratio] layouts-ratio]
      (.addLayout lay a-lay ratio))
    lay))

(defn auto-layout!
  "GraphModel -> Long -> [[AbastractLayout Double]] -> AutoLayout"
  [gm milliseconds layouts-ratio]
  (let [lay (auto-layout milliseconds layouts-ratio)]
    (.setGraphModel lay gm)
    (.execute lay)
    lay))
