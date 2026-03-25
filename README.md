Markdown
# Concurrent Job Shop Scheduling System

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Concurrency](https://img.shields.io/badge/Concurrency-Extrinsic%20Monitor-blue.svg)
![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)

## 📌 Overview

This project implements a highly robust, thread-safe **Job Shop Scheduling Manager** designed to synchronize the usage of manufacturing machines (e.g., 3D printers, laser cutters) for executing concurrent job requests. 

Developed entirely from scratch in Java 21, the system utilizes the **Extrinsic Monitor Pattern** to manage shared resources efficiently without relying on higher-level concurrent collections or synchronized blocks. It strictly enforces Head-of-Line blocking and prevents common concurrency pitfalls such as lost signals, spurious wakeups, and deadlocks.

## ✨ Key Technical Highlights

* **Extrinsic Monitor Implementation:** Exclusively uses `java.util.concurrent.locks.ReentrantLock` and `Condition` to protect shared states.
* **Fine-Grained Thread Signaling:** Avoids the anti-pattern of `signalAll()`. Instead, it maps individual `Condition` variables to specific machine instances (Per-Machine Condition), ensuring precise, targeted thread wakeups and eliminating CPU-wasting spurious wakeups.
* **Dual Scheduling Algorithms:**
  * **FCFS (First-Come-First-Served):** Enforces strict chronological processing with precise Head-of-Line blocking.
  * **SJF (Shortest Job First):** Dynamically sorts pending jobs based on the total processing time of their operations to optimize throughput.
* **Zero Busy-Waiting:** Threads yield the CPU entirely while waiting for resource allocation.
* **Defensive Programming & Null Safety:** 100% local exception handling. The system elegantly digests malformed or null requests without throwing unchecked exceptions, guaranteeing continuous uptime.

## 🏗️ Architecture & Core Logic

The core scheduling engine (`JobShopManager.java`) manages two asynchronous event streams: incoming jobs and arriving machines. 

1. **State Management:** Maintains decoupled data structures for `pendingJobs` (waiting queue) and `availableMachines` (idle resources).
2. **The `trySchedule()` Engine:** An atomic execution block triggered on any state mutation. It evaluates the head of the queue against available resources.
3. **Allocation Protocol:** Upon a successful match, the system securely transfers the job assignment to an intermediate `machineAllocations` map and signals the exact memory address of the target machine thread.

## 🚀 Getting Started

### Prerequisites
* Java Development Kit (JDK) 21 or higher.

### Compilation
Clone the repository and compile the source code from the root directory:
```bash
javac src/*.java
Running the Test Suite
The project includes a comprehensive multithreaded test suite (Tests.java) that simulates complex, out-of-order execution scenarios (UR1 to UR6), verifying the robustness of the scheduler under heavy contention.

To execute the tests:

Bash
java -cp src App
Expected Test Coverage
UR1-UR2: Standard job and machine synchronization.

UR3: Deferred machine arrivals (Jobs wait for resources).

UR4: Highly interleaved, random-order arrivals of machines and jobs.

UR5: Verification of strict allocation mapping (Job Names returned securely to threads).

UR6: SJF priority sorting under resource contention.