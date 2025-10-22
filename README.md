# clj-gephi

A Clojure wrapper for the [Gephi Toolkit](https://github.com/gephi/gephi-toolkit), providing idiomatic Clojure access to powerful network analysis and visualization capabilities.

[![Clojars Project](https://img.shields.io/clojars/v/clj-gephi.svg)](https://clojars.org/clj-gephi)
[![License](https://img.shields.io/badge/License-EPL%201.0-blue.svg)](https://opensource.org/licenses/EPL-1.0)

## Table of Contents

- [About](#about)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Usage Examples](#usage-examples)
  - [Creating and Manipulating Graphs](#creating-and-manipulating-graphs)
  - [Importing and Exporting](#importing-and-exporting)
  - [Network Statistics](#network-statistics)
  - [Filtering](#filtering)
  - [Layouts](#layouts)
  - [Visual Styling](#visual-styling)
- [API Overview](#api-overview)
- [Complete Example](#complete-example)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## About

**clj-gephi** is a comprehensive Clojure library that wraps the Gephi Toolkit, enabling programmatic network analysis and visualization. [Gephi](https://gephi.org/) is a leading open-source platform for visualizing and analyzing large network graphs.

This library allows you to:
- Create, import, and export network graphs
- Calculate network metrics (degree, PageRank, betweenness centrality, modularity, etc.)
- Apply filters to focus on specific subgraphs
- Run layout algorithms (Force Atlas, Yifan Hu, and more)
- Style nodes and edges based on network properties
- Export visualizations in multiple formats (PNG, SVG, PDF, GEXF)

## Features

- **Graph Operations**: Create directed and undirected graphs, add/remove nodes and edges
- **Import/Export**: GEXF, GraphML, and other standard graph formats
- **Statistics**:
  - Degree distribution
  - PageRank
  - Betweenness centrality
  - Graph distance metrics (diameter, radius, average path length)
  - Community detection (Modularity)
- **Filtering**: Filter nodes by degree and other criteria
- **Layouts**:
  - Force Atlas (force-directed)
  - Yifan Hu
  - Noverlap (prevent node overlaps)
  - Label Adjust
- **Appearance**: Color and size nodes based on metrics or communities
- **Preview**: Configure visualization settings

## Requirements

- **Java**: JDK 8 or higher
- **Clojure**: 1.11.1 or higher
- **Gephi Toolkit**: 0.10.1 (automatically resolved)

## Installation

Add the following dependency to your `project.clj`:

```clojure
[clj-gephi "0.1.0-SNAPSHOT"]
```

Or for `deps.edn`:

```clojure
{:deps {clj-gephi {:mvn/version "0.1.0-SNAPSHOT"}}}
```

## Quick Start

```clojure
(ns my-network-analysis
  (:require [clj-gephi.project :as p]
            [clj-gephi.graph :as g]
            [clj-gephi.statistics :as stat]
            [clj-gephi.io.export :as exp]))

;; Create a new project
(p/new-project!)
(let [wp (p/current-workspace)
      gm (g/graph-model wp)
      graph (g/undirected-graph gm)]

  ;; Create nodes
  (let [alice (g/node! graph "alice" "Alice")
        bob (g/node! graph "bob" "Bob")
        charlie (g/node! graph "charlie" "Charlie")]

    ;; Create edges
    (g/edge! graph alice bob false)
    (g/edge! graph alice charlie false)
    (g/edge! graph bob charlie false))

  ;; Calculate statistics
  (let [degree-stat (stat/degree! gm)]
    (println "Average degree:" (stat/average-degree degree-stat)))

  ;; Export
  (exp/export-graph-file! "my-network.gexf" "gexf" wp true))
```

## Usage Examples

### Creating and Manipulating Graphs

```clojure
(require '[clj-gephi.project :as p]
         '[clj-gephi.graph :as g])

;; Initialize project
(p/new-project!)
(let [wp (p/current-workspace)
      gm (g/graph-model wp)
      graph (g/directed-graph gm)]

  ;; Add nodes
  (def n1 (g/node! graph "node1" "Node 1"))
  (def n2 (g/node! graph "node2" "Node 2"))
  (def n3 (g/node! graph "node3" "Node 3"))

  ;; Add edges (directed)
  (g/edge! graph n1 n2 true)
  (g/edge! graph n2 n3 true)
  (g/edge! graph n3 n1 true)

  ;; Query graph
  (println "Nodes:" (g/node-count graph))
  (println "Edges:" (g/edge-count graph))
  (println "Has node1?" (g/has-node? graph "node1")))
```

### Importing and Exporting

```clojure
(require '[clj-gephi.io.import :as imp]
         '[clj-gephi.io.export :as exp])

;; Import a GEXF file
(imp/import-graph-file! wp "data/network.gexf" false)

;; Export to various formats
(exp/export-graph-file! "output.png")           ; PNG image
(exp/export-graph-file! "output.svg")           ; SVG vector
(exp/export-graph-file! "output.pdf" wp)        ; PDF
(exp/export-graph-file! "output.gexf" "gexf" wp true) ; GEXF (visible only)
```

### Network Statistics

```clojure
(require '[clj-gephi.statistics :as stat])

;; Calculate various metrics
(let [graph (g/undirected-graph gm)
      degree-stat (stat/degree! gm)
      pagerank-stat (stat/pagerank! gm false)
      distance-stat (stat/distance! gm false)
      modularity-stat (stat/modularity! gm)]

  ;; Basic metrics
  (println "Nodes:" (g/node-count graph))
  (println "Edges:" (g/edge-count graph))
  (println "Average degree:" (stat/average-degree degree-stat))

  ;; Distance metrics
  (println "Diameter:" (stat/diameter distance-stat))
  (println "Radius:" (stat/radius distance-stat))
  (println "Average distance:" (stat/avg-distance distance-stat))

  ;; Export HTML reports
  (spit "degree-report.html" (stat/html-report degree-stat))
  (spit "pagerank-report.html" (stat/html-report pagerank-stat)))
```

### Filtering

```clojure
(require '[clj-gephi.filters :as f])

;; Filter nodes by minimum degree
(f/filter-by-degree! gm (f/visible-view gm) 3)

;; Filter by degree range
(f/filter-by-degree! gm (f/visible-view gm) 2 10)

;; Work with filtered graph
(let [filtered-graph (g/visible-graph gm)]
  (println "Filtered nodes:" (g/node-count filtered-graph))
  (println "Filtered edges:" (g/edge-count filtered-graph)))
```

### Layouts

```clojure
(require '[clj-gephi.layout :as lay])

;; Force Atlas layout
(let [force-opts {:attraction-strength 1.5
                  :repulsion-strength 0.5
                  :gravity 1.0}]
  (lay/force-atlas! gm 50 force-opts))

;; Yifan Hu layout
(let [yifan-opts {:step-displacement 1.0
                  :optimal-distance 200.0}]
  (lay/yifan-hu! gm 50 yifan-opts))

;; Prevent overlaps
(let [noverlap-opts {:margin 10.0
                     :ratio 2.0}]
  (lay/noverlap! gm 50 noverlap-opts))

;; Adjust labels
(let [label-opts {:adjust-by-size? true}]
  (lay/label-adjust! gm 500 label-opts))

;; Multi-stage layout pipeline
(lay/force-atlas! gm 50 {:attraction-strength 1.5 :repulsion-strength 0.5})
(lay/yifan-hu! gm 50 {:step-displacement 1.0 :optimal-distance 200.0})
(lay/noverlap! gm 50 {:margin 5.0 :ratio 1.5})
(lay/label-adjust! gm 100 {:adjust-by-size? true})
```

### Visual Styling

```clojure
(require '[clj-gephi.appearance :as app]
         '[clj-gephi.preview :as prev])
(import '[java.awt Color])

;; Style nodes
(let [graph (g/visible-graph gm)
      am (app/appearance-model)]

  ;; Color by community (modularity)
  (app/color-by-modularity! am graph gm)

  ;; Size by PageRank
  (app/size-by-pagerank! graph am gm 5 30)

  ;; Or size by degree
  (app/size-by-degree! graph am gm 8 20))

;; Configure preview settings
(let [pm (prev/preview-model)
      font (-> (prev/node-font-label pm)
               (.deriveFont 8.0))]
  (prev/show-node-labels! pm true)
  (prev/edge-color! pm Color/GRAY)
  (prev/edge-thickness! pm 0.5)
  (prev/node-font-label! pm font))
```

## API Overview

### Namespaces

| Namespace | Description |
|-----------|-------------|
| `clj-gephi.project` | Project and workspace management |
| `clj-gephi.graph` | Graph creation and manipulation |
| `clj-gephi.io.import` | Import graphs from files |
| `clj-gephi.io.export` | Export graphs and visualizations |
| `clj-gephi.statistics` | Calculate network metrics |
| `clj-gephi.filters` | Filter graphs by properties |
| `clj-gephi.layout` | Apply layout algorithms |
| `clj-gephi.appearance` | Style nodes and edges |
| `clj-gephi.preview` | Configure preview settings |

### Key Functions

**Graph Operations:**
- `g/node!`, `g/edge!` - Create nodes and edges
- `g/node-count`, `g/edge-count` - Count elements
- `g/directed-graph`, `g/undirected-graph` - Get graph views
- `g/all-nodes`, `g/all-edges` - Get all elements

**Statistics:**
- `stat/degree!` - Degree distribution
- `stat/pagerank!` - PageRank centrality
- `stat/distance!` - Graph distance metrics
- `stat/modularity!` - Community detection

**Layouts:**
- `lay/force-atlas!` - Force-directed layout
- `lay/yifan-hu!` - Yifan Hu layout
- `lay/noverlap!` - Prevent node overlaps
- `lay/label-adjust!` - Adjust label positions

**Appearance:**
- `app/color-by-modularity!` - Color by community
- `app/size-by-pagerank!` - Size by centrality
- `app/size-by-degree!` - Size by degree

## Complete Example

Here's a complete workflow from graph creation to export:

```clojure
(ns network-analysis.core
  (:require [clj-gephi.project :as p]
            [clj-gephi.graph :as g]
            [clj-gephi.statistics :as stat]
            [clj-gephi.filters :as f]
            [clj-gephi.layout :as lay]
            [clj-gephi.appearance :as app]
            [clj-gephi.preview :as prev]
            [clj-gephi.io.import :as imp]
            [clj-gephi.io.export :as exp])
  (:import [java.awt Color]))

(defn analyze-network [input-file output-prefix]
  ;; 1. Create project
  (p/new-project!)
  (let [wp (p/current-workspace)
        gm (g/graph-model wp)]

    ;; 2. Import graph
    (imp/import-graph-file! wp input-file false)

    ;; 3. Calculate statistics
    (let [graph (g/undirected-graph gm)
          degree-stat (stat/degree! gm)
          pagerank-stat (stat/pagerank! gm false)
          distance-stat (stat/distance! gm false)
          modularity-stat (stat/modularity! gm)]

      ;; Print metrics
      (println "Network Analysis Results:")
      (println "  Nodes:" (g/node-count graph))
      (println "  Edges:" (g/edge-count graph))
      (println "  Average degree:" (stat/average-degree degree-stat))
      (println "  Diameter:" (stat/diameter distance-stat))
      (println "  Average distance:" (stat/avg-distance distance-stat))

      ;; Save reports
      (spit (str output-prefix "-degree.html") (stat/html-report degree-stat))
      (spit (str output-prefix "-pagerank.html") (stat/html-report pagerank-stat)))

    ;; 4. Filter graph (keep well-connected nodes)
    (f/filter-by-degree! gm (f/visible-view gm) 3)

    ;; 5. Apply layouts
    (lay/force-atlas! gm 50 {:attraction-strength 1.5 :repulsion-strength 0.5})
    (lay/yifan-hu! gm 50 {:step-displacement 1.0 :optimal-distance 200.0})
    (lay/noverlap! gm 50 {:margin 10.0 :ratio 2.0})
    (lay/label-adjust! gm 100 {:adjust-by-size? true})

    ;; 6. Style visualization
    (let [graph (g/visible-graph gm)
          am (app/appearance-model)]
      (app/color-by-modularity! am graph gm)
      (app/size-by-pagerank! graph am gm 5 30))

    ;; 7. Configure preview
    (let [pm (prev/preview-model)]
      (prev/show-node-labels! pm true)
      (prev/edge-color! pm Color/LIGHT_GRAY)
      (prev/edge-thickness! pm 0.5))

    ;; 8. Export results
    (exp/export-graph-file! (str output-prefix ".png"))
    (exp/export-graph-file! (str output-prefix ".svg"))
    (exp/export-graph-file! (str output-prefix ".gexf") "gexf" wp true)

    (println "Analysis complete! Files saved with prefix:" output-prefix)))

;; Usage
(analyze-network "data/social-network.gexf" "output/analysis")
```

## Testing

The library includes a comprehensive test suite with 60+ tests covering all major functionality. Run tests with:

```bash
lein test
```

See `test/README.md` for detailed information about the test suite, including realistic network analysis scenarios.

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Write tests for your changes
4. Ensure all tests pass (`lein test`)
5. Follow idiomatic Clojure style
6. Submit a pull request

## Resources

- [Gephi Website](https://gephi.org/)
- [Gephi Toolkit on GitHub](https://github.com/gephi/gephi-toolkit)
- [Gephi Toolkit Documentation](https://gephi.org/toolkit/)
- [Network Analysis Tutorials](https://gephi.org/tutorials/)

## License

Copyright © 2016-2025 Guillaume Érétéo

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

## Acknowledgments

This project is a Clojure wrapper around the excellent [Gephi Toolkit](https://github.com/gephi/gephi-toolkit). All credit for the underlying network analysis and visualization capabilities goes to the Gephi development team.
