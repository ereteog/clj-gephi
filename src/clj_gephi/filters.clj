(ns clj-gephi.filters
  (import [org.openide.util Lookup])
  (import [org.gephi.filters.api FilterController])
  (import [org.gephi.filters.api Query])
  (import [org.gephi.filters.api Range])
  (import [org.gephi.filters.plugin.graph DegreeRangeBuilder$DegreeRangeFilter])
  )

(def fc (.lookup (Lookup/getDefault) FilterController))

(defn view-by-degree
  "GraphModel -> GraphView -> int -> int-> GraphView"
  ([gm view min-degree max-degree]
   (let [graph (if (nil? view)
                 (.getGraph gm)
                 (.getGraph gm view))
         drf (DegreeRangeBuilder$DegreeRangeFilter.)]
     (.init drf graph)
     (.setRange drf (Range. (int min-degree) (int max-degree)))
     (->> (.createQuery fc drf)
          (.filter fc))))
  ([gm view min-degree]
   (view-by-degree gm view min-degree Integer/MAX_VALUE)))

(defn visible-view
  "GraphModel -> GraphView"
  [gm]
  (.getVisibleView gm))

(defn set-visible-view!
  "GraphModel -> GraphView -> GraphModel"
  [gm view]
  (.setVisibleView gm view)
  gm)

(defn filter-by-degree!
  "GraphModel -> GraphView -> int -> int-> GraphModel"
  ([gm view min-degree max-degree]
   (->> (view-by-degree gm view min-degree max-degree)
        (set-visible-view! gm)))
   ([gm view min-degree]
    (filter-by-degree! gm view min-degree Integer/MAX_VALUE)))

