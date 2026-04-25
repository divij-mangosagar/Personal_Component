
```
src
└───components
    └───datasetrv
            DatasetRV.java
            DatasetRV1L.java
            DatasetRVKernel.java
            DatasetRVSecondary.java
```

---
| File Name | Description |
|---------|-------------|
| **DatasetRV** | The extended interface with the secondary component methods |
| **DatasetRV1L** | Component class that implements the kernel interface, DatasetRVKernel |
| **DatasetRVKernel** | Primary interface containing the kernel methods |
| **DatasetRVSecondary** | Abstracted class containing the implementation of the secondary methods in DatasetRV|
---

Client class placed outside of the src folder,titled as NormalDistribution
