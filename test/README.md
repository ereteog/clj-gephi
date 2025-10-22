# clj-gephi Test Suite

Comprehensive test suite for the clj-gephi library, covering all major functionality with realistic network analysis scenarios.

## Test Files

### 1. `clj_gephi/graph_test.clj`
**Graph Operations Tests**

Tests core graph manipulation functionality:
- Node creation from GraphModel and Graph instances
- Node operations (adding, counting, retrieval)
- Edge creation and operations
- Different graph types (directed, undirected)
- Graph traversal (all nodes/edges)
- Clearing nodes
- Realistic scenario: Building a simple social network

**Key Tests:**
- `test-node-creation` - Creating nodes with IDs and labels
- `test-edge-operations` - Building edges between nodes
- `test-graph-types` - Working with directed and undirected graphs
- `test-simple-network` - Creating a friendship network

### 2. `clj_gephi/statistics_test.clj`
**Network Statistics Tests**

Tests statistical analysis capabilities:
- Degree calculations and average degree
- PageRank (directed and undirected)
- Graph distance metrics (diameter, radius, average path length)
- Betweenness centrality
- Modularity and community detection
- HTML report generation

**Key Test Scenarios:**
- Star networks (1 hub + peripheral nodes)
- Directed chains
- Triangle networks (complete subgraphs)
- Two-community networks with bridges
- Comprehensive multi-metric analysis

**Key Tests:**
- `test-degree-calculation` - Calculating degree statistics on star network
- `test-graph-distance-undirected` - Distance metrics validation
- `test-modularity-two-communities` - Community detection
- `test-comprehensive-network-analysis` - Full statistical analysis

### 3. `clj_gephi/filters_test.clj`
**Graph Filtering Tests**

Tests filtering functionality:
- Filtering by minimum degree
- Filtering by degree range
- Creating and managing graph views
- Sequential filter application
- Preservation of original graph

**Key Test Scenarios:**
- Networks with varying node degrees
- Star networks with high-degree hubs
- Sequential filtering (progressive filtering)

**Key Tests:**
- `test-filter-by-minimum-degree` - Basic degree filtering
- `test-filter-preserves-original-graph` - Non-destructive filtering
- `test-filter-on-star-network` - Filtering hub networks

### 4. `clj_gephi/layout_test.clj`
**Graph Layout Tests**

Tests layout algorithm functionality:
- Force Atlas layout (force-directed)
- Yifan Hu layout
- Noverlap layout (preventing overlaps)
- Label Adjust layout
- Sequential layout application
- Layout pipelines with multiple stages

**Key Test Scenarios:**
- Simple connected networks
- Larger networks (20+ nodes)
- Realistic multi-stage layout pipelines

**Key Tests:**
- `test-yifan-hu-layout-execution` - Running Yifan Hu algorithm
- `test-force-atlas-layout-execution` - Running Force Atlas
- `test-sequential-layouts` - Applying multiple layouts
- `test-realistic-layout-pipeline` - Complete layout workflow

### 5. `clj_gephi/io_test.clj`
**Import/Export Tests**

Tests file I/O operations:
- Importing GEXF files
- Exporting to GEXF format
- Directed vs undirected import
- Import-export roundtrip preservation
- Exporting visible graph only
- Workspace-specific exports

**Test Fixtures:**
- `test/resources/sample-network.gexf` - 5-node test network

**Key Tests:**
- `test-import-gexf-file` - Loading network from file
- `test-export-gexf-file` - Saving network to file
- `test-import-export-roundtrip` - Verifying data preservation

### 6. `clj_gephi/appearance_test.clj`
**Visual Appearance Tests**

Tests node styling functionality:
- Appearance model creation
- Ranking functions (degree, PageRank, betweenness)
- Node sizing by metrics
- Node coloring by metrics
- Partition-based coloring (communities)
- Color palette generation
- Combined styling (size + color)

**Key Test Scenarios:**
- Hub-and-spoke networks
- Multi-community networks
- Various centrality metrics

**Key Tests:**
- `test-size-by-pagerank` - Sizing nodes by importance
- `test-color-by-modularity` - Coloring by community
- `test-combined-appearance-styling` - Multiple transformations

### 7. `clj_gephi/integration_test.clj`
**End-to-End Integration Tests**

Tests complete network analysis workflows:
- Full pipeline: create → analyze → filter → layout → style → export
- Import → analyze → export workflow
- Multi-workspace scenarios
- Comprehensive metric analysis

**Key Test Scenarios:**
- **Collaboration Network**: 13-node research collaboration network with:
  - 3 research groups (AI, Systems, Theory)
  - Bridge researchers connecting groups
  - Peripheral researchers with few connections

**Key Tests:**
- `test-complete-network-analysis-workflow` - End-to-end analysis
- `test-import-analyze-export-workflow` - Processing existing networks
- `test-multi-workspace-workflow` - Managing multiple graphs
- `test-network-metrics-comprehensive` - All available metrics

## Running Tests

### Run all tests:
```bash
lein test
```

### Run specific test namespace:
```bash
lein test clj-gephi.graph-test
lein test clj-gephi.statistics-test
lein test clj-gephi.integration-test
```

### Run specific test:
```bash
lein test :only clj-gephi.integration-test/test-complete-network-analysis-workflow
```

## Test Coverage

The test suite covers:

✅ **Graph Operations** (9 tests)
- Node and edge creation
- Graph types (directed/undirected)
- Graph manipulation

✅ **Statistics** (10 tests)
- Degree, PageRank, Betweenness
- Graph distance metrics
- Community detection (Modularity)

✅ **Filtering** (6 tests)
- Degree-based filtering
- Graph views
- Non-destructive filtering

✅ **Layouts** (10 tests)
- Force Atlas, Yifan Hu
- Noverlap, Label Adjust
- Multi-stage pipelines

✅ **I/O Operations** (6 tests)
- GEXF import/export
- Roundtrip preservation
- Workspace management

✅ **Appearance** (14 tests)
- Node sizing by metrics
- Node coloring
- Community coloring
- Palette generation

✅ **Integration** (5 tests)
- Complete workflows
- Realistic scenarios
- Multi-workspace operations

**Total: 60+ comprehensive tests**

## Realistic Network Scenarios

### Social Network (graph_test.clj)
Simple friendship network with 4 people and their connections.

### Star Network (statistics_test.clj, filters_test.clj)
Central hub connected to peripheral nodes - common in organizational hierarchies.

### Chain Network (statistics_test.clj)
Linear directed chain - useful for process flows.

### Two-Community Network (statistics_test.clj)
Two dense subgraphs connected by a bridge - demonstrates community detection.

### Collaboration Network (integration_test.clj)
Research collaboration network with:
- Multiple research groups
- Bridge researchers
- Peripheral collaborators
- Realistic degree distributions

## Test Data Files

### `test/resources/sample-network.gexf`
5-node undirected network for import/export testing:
- 5 nodes: alice, bob, charlie, diana, eve
- 6 edges forming a connected network

## Dependencies Required for Testing

The tests require:
- Clojure 1.11.1
- Gephi Toolkit 0.10.1
- clojure.test (built-in)

All Gephi toolkit dependencies are automatically resolved through Leiningen.

## Notes

- Tests create temporary files in `test/resources/` which are automatically cleaned up
- Each test creates its own project and workspace for isolation
- Tests are independent and can run in any order
- Some tests verify Gephi 0.10.1 specific behavior
