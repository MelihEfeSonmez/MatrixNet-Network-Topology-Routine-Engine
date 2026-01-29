# MatrixNet: High-Performance Network Simulation & Topology Analysis Engine

MatrixNet is a specialized Java-based simulation engine designed for modeling, analyzing, and hardening clandestine overlay networks. It provides a robust framework for managing complex network topologies, calculating optimal communication routes under multi-objective constraints, and identifying critical infrastructure vulnerabilities.

<p align="center">
  <img src="images/Trinity.png" alt="Trinity" width="700">
  <br>
  <em>Figure 1: ”There is no spoon.” Trinity uses a phone booth to exit the Matrix while Agents close in.</em>
</p>

## 📂 Architecture & Custom Data Structures

To ensure maximum performance, low memory overhead, and full control over algorithmic execution, this engine is built without standard Java collection libraries.

* **`MyHashMap.java`**: A custom high-performance hash map implementation for O(1) average-time lookups of network hosts and link data.
* **`MyMinHeap.java`**: A specialized priority queue (binary heap) optimized for the Dijkstra-based routing engine.
* **`Router.java`**: The core pathfinding module that handles multi-objective graph optimization.
* **`NetManager.java`**: Manages the global network state, including host lifecycle and bidirectional link synchronization.

## 🚀 Key Capabilities

### 1. Dynamic Topology Management
* **Host Spawning**: Dynamically initialize access points with unique identifiers and varying security clearance levels.
* **Link Control**: Establish and seal/unseal bidirectional tunnels with specific latency, bandwidth, and firewall parameters.

### 2. Multi-Objective Routing Engine
The engine calculates optimal covert routes based on a sophisticated priority system:
* **Dynamic Latency & Congestion**: Models network load using a congestion factor ($\lambda$) that increases effective latency per hop.
* **Constraint Satisfaction**: Routes must respect minimum bandwidth requirements and per-hop firewall/clearance compatibility.
* **Optimization Priority**: Lowest total dynamic latency > Minimum segments > Lexicographical host sequence.

### 3. Network Resilience & Vulnerability Analysis
The engine supports advanced graph analysis to detect single points of failure:
* **Articulation Point Detection**: Identifies critical hosts whose failure would fragment the network into multiple disconnected components.
* **Bridge Identification**: Detects critical links (bridges) that are essential for maintaining network connectivity.
* **Connectivity Scans**: Analyzes the infrastructure to report the number of connected components and cyclic structures.

## 🛠️ Usage

### Compilation
The engine requires a Java environment. Compile all modules using:
```bash
javac *.java
```
### Execution
Run the simulation by providing an instruction script and a target log file:

```bash
java Main <input_script> <output_log>
```

## 📊 Technical Specifications

* **Performance**: Engineered for high-throughput environments, capable of processing up to 500,000 simulation commands in under 30 seconds.
* **Precision**: All arithmetic reporting utilizes high-precision rounding (HALF_UP) for reliability.
* **Validation**: Built-in semantic and state validation to ensure network integrity during rapid topology changes.

## 🧪 Testing & Automation

This project includes a custom Python test runner (`test_runner.py`) to automate compilation, execution, and output verification against the provided test cases.

### Prerequisites
* Python 3.x
* Java Development Kit (JDK)

### Directory Structure for Testing
Ensure your Java source files are in the `src` folder and test cases are in the `test_cases` folder as follows:
```text
.
├── src/ (Java files)
├── test_cases/
│   └── TypeX/ (inputs/*.txt & outputs/*.txt)
└── test_runner.py
```
Running Tests
You can run the full test suite or filter by specific criteria:

```bash
# Run all test cases
python test_runner.py

# Run only Type 2 cases (Routing)
python test_runner.py --type type2

# Benchmark mode (Measure time performance without output comparison)
python test_runner.py --benchmark

# Verbose mode (Show detailed diffs for failed tests)
python test_runner.py --verbose
```
The script automatically handles:
* ** Java compilation.
* ** Line-ending normalization (CRLF/LF) for cross-platform compatibility.
* ** 30-second timeout constraints per test case.

---
*Developed by Melih Efe Sonmez.*
