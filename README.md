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

## Mathematical Background

### Sample Mean

\[
\bar{x} = \frac{1}{n}\sum_{i=1}^{n} x_i
\]

### Sample Variance (Unbiased)

\[
s^2 = \frac{1}{n-1}\sum_{i=1}^{n} (x_i - \bar{x})^2
\]

### Sample Standard Deviation

\[
s = \sqrt{s^2}
\]

### Covariance

\[
\text{Cov}(X,Y) = \frac{1}{n-1}\sum_{i=1}^{n} (x_i - \bar{x})(y_i - \bar{y})
\]

### Pearson Correlation

\[
\rho_{X,Y} = \frac{\text{Cov}(X,Y)}{s_X s_Y}
\]

### Maximum Likelihood Estimation for Normal Distribution

\[
\hat{\mu} = \frac{1}{n}\sum_{i=1}^{n} x_i
\]

\[
\hat{\sigma}^2 = \frac{1}{n}\sum_{i=1}^{n} (x_i - \hat{\mu})^2
\]

### Box-Muller Transform (Standard Normal Sampling)

\[
Z_1 = \sqrt{-2 \ln U_1} \cos(2\pi U_2)
\]
\[
Z_2 = \sqrt{-2 \ln U_1} \sin(2\pi U_2)
\]

where \(U_1, U_2 \sim \text{Uniform}(0,1)\).

---

## Getting Started

### Prerequisites

- Java 11 or higher
- OSU Components library (provided in CSE 2231 course environment)
- JUnit 4 for testing

