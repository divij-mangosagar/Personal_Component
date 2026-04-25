# DatasetRV: A Multivariate Statistical Component for CSE 2231

## Overview

**DatasetRV** is a Java component that stores and analyzes multivariate datasets. It provides:

- Storage for multiple random variables (rows) with multiple observations (columns)
- Descriptive statistics (mean, variance, standard deviation)
- Multivariate analysis (covariance matrix, correlation matrix)
- Distribution fitting (Normal distribution parameter estimation)
- Random sampling from fitted distributions
- Probability calculations using Monte Carlo simulation

This component was developed as the **portfolio project** for CSE 2231 (Software II) at The Ohio State University, following the OSU component design pattern.

---

## Features

| Feature | Description |
|---------|-------------|
| **Data Storage** | Store multiple variables with observations using `Sequence<Sequence<Double>>` |
| **Descriptive Statistics** | Sample mean, variance, standard deviation for any variable |
| **Frequency Analysis** | Calculate frequency of observations with optional combination |
| **Moments** | First four statistical moments (mean, variance, skewness, kurtosis) |
| **Multivariate Analysis** | Full covariance and correlation matrices |
| **Subset Analysis** | Compute covariance/correlation for selected variables only |
| **Distribution Fitting** | Estimate Normal distribution parameters from data |
| **Random Sampling** | Generate new samples from fitted Normal distributions (univariate and multivariate) |
| **Probability Calculation** | Monte Carlo estimation of probabilities within bounds |

---

## Component Architecture

The component follows the OSU layered design:

| Layer | File | Responsibility |
|-------|------|----------------|
| Kernel Interface | `DatasetRVKernel.java` | Declares primitive methods (`addElement`, `removeElement`, `getElement`) |
| Secondary Interface | `DatasetRV.java` | Declares convenience methods (`sampleMean`, `covarianceMatrix`) |
| Abstract Class | `DatasetRVSecondary.java` | Implements secondary methods using only kernel methods |
| Concrete Class | `DatasetRV1L.java` | Implements kernel methods using `Sequence<Sequence<Double>>` storage |
| Client Class | `NormalDistribution.java` | Example client using the component for Normal distribution analysis |

---


## Getting Started

### Prerequisites

- Java 11 or higher
- OSU Components library (provided in CSE 2231 course environment)
- JUnit 4 for testing

## Instalation 

 - Clone the git repo: https://github.com/divij-mangosagar/Personal_Component.git

