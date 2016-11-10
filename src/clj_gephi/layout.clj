(ns clj-gephi.layout
  (:require [schema.core :as s])
  (:import [org.gephi.layout.plugin.force StepDisplacement])
  (:import [org.gephi.layout.plugin.force.yifanHu YifanHuLayout])
  (:import [org.gephi.layout.plugin.forceAtlas ForceAtlasLayout])
  (:import [org.gephi.layout.plugin AutoLayout])
  (:import [java.util.concurrent TimeUnit])
  (:import [org.gephi.layout.plugin.noverlap NoverlapLayout])
  (:import [org.gephi.layout.plugin.labelAdjust LabelAdjust])
  )

;; https://gephi.org/tutorials/gephi-tutorial-layouts.pdf

(s/defschema YifanHuOpts
  {
   :step-displacement                       s/Num
   (s/optional-key :adaptive-cooling?)      s/Bool
   (s/optional-key :barnes-hut-theta)       s/Num
   (s/optional-key :convergence-threshold)  s/Num
   (s/optional-key :initial-step)           s/Num
   (s/optional-key :optimal-distance)       s/Num
   (s/optional-key :quad-tree-max-level)    s/Num
   (s/optional-key :relative-strength)      s/Num                                         (s/optional-key :step)                   s/Num
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

(s/defschema NoverlapOpts
  {
   (s/optional-key :margin)                            s/Num
   (s/optional-key :ratio)                             s/Num
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
    (some->> adaptive-cooling?  (.setAdaptiveCooling lay))
    (some->> barnes-hut-theta  (.setBarnesHutTheta lay))
    (some->> convergence-threshold (.setConvergenceThreshold lay))
    (some->> initial-step       (.setInitialStep lay))
    (some->> optimal-distance (.setOptimalDistance lay))
    (some->> quad-tree-max-level (.setQuadTreeMaxLevel lay))
    (some->> relative-strength (.setRelativeStrength lay))
    (some->> step (.setStep lay))
    (some->> step-ratio (.setStepRatio lay))
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
    (some->> adjust-sizes? (.setAdjustSizes lay))
    (some->> attraction-strength	(.setAttractionStrength lay))
    (some->> cooling (.setCooling lay))
    (some->> freeze-balance? (.setFreezeBalance lay))
    (some->> freeze-inertia	(.setFreezeInertia lay))
    (some->> freeze-strength (.setFreezeStrength lay))
    (some->> gravity (.setGravity lay))
    (some->> inertia	(.setInertia lay))
    (some->> max-displacement	(.setMaxDisplacement lay))
    (some->> outbound-attraction-distribution
             (.setOutboundAttractionDistribution lay))
    (some->> repulsion-strength	(.setRepulsionStrength lay))
    (some->> speed	(.setSpeed lay))
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
    (some->> adjust-by-size?	(.setAdjustBySize lay))
    (some->> speed	(.setSpeed lay))
    lay))

(defn label-adjust
  "LabelAdjustOpts -> LabelAdjust"
  [opts]
  (let [lay (LabelAdjust. nil)]
    (.resetPropertiesValues lay)
    (update-label-adjust! lay opts)
    lay))

(defn update-noverlap!
  "NoverlapLayout -> NoverlapOpts -> NoverlapLayout"
  [lay opts]
  (s/validate NoverlapOpts opts)
  (let [{margin :margin
         ratio  :ratio
         speed  :speed} opts]
    (some->> margin (.setMargin lay))
    (some->> ratio(.setRatio lay))
    (some->> speed (.setSpeed lay))
    lay))

(defn noverlap
  "LabelAdjustOpts -> LabelAdjust"
  [opts]
  (let [lay (NoverlapLayout. nil)]
    (.resetPropertiesValues lay)
    (update-noverlap! lay opts)
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

(defn noverlap!
  "GraphModel -> Noverlap"
  [gm nb-loops opts]
  (->> (noverlap opts)
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
