package com.syos.usecases.reports;

import com.syos.entities.Product;
import com.syos.entities.StockBatch;
import com.syos.usecases.repositories.ProductRepository;
import com.syos.usecases.repositories.StockBatchRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Stock report showing batch-wise details.
 * Requirement: "Stock report. Provides details of the current stock batch-wise
 * with the same
 * information as mentioned in 2(a) above" (code, date of purchase, quantity,
 * expiry date)
 */
public class StockReport extends ReportTemplate {
    private final StockBatchRepository stockBatchRepository;
    private final ProductRepository productRepository;

    public StockReport(StockBatchRepository stockBatchRepository, ProductRepository productRepository) {
        this.stockBatchRepository = stockBatchRepository;
        this.productRepository = productRepository;
    }

    @Override
    protected String getReportHeader() {
        return "=== STOCK REPORT (BATCH-WISE) ===\n";
    }

    @Override
    protected String getReportBody() {
        List<StockBatch> batches = stockBatchRepository.findAll();

        if (batches.isEmpty()) {
            return "No stock batches available.";
        }

        StringBuilder body = new StringBuilder();
        body.append(String.format("%-12s %-25s %-15s %-10s %-15s %-10s\n",
                "Code", "Product Name", "Purchase Date", "Quantity", "Expiry Date", "Status"));
        body.append("-".repeat(100)).append("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (StockBatch batch : batches) {
            String productName = productRepository.findByCode(batch.getProductCode())
                    .map(Product::getName)
                    .orElse("Unknown");

            String status = batch.isExpired() ? "EXPIRED" : (batch.getDaysUntilExpiry() < 30 ? "EXPIRING SOON" : "OK");

            body.append(String.format("%-12s %-25s %-15s %-10d %-15s %-10s\n",
                    batch.getProductCode(),
                    productName,
                    batch.getPurchaseDate().format(formatter),
                    batch.getQuantity(),
                    batch.getExpiryDate().format(formatter),
                    status));
        }

        body.append("-".repeat(100)).append("\n");
        body.append(String.format("Total batches: %d\n", batches.size()));

        return body.toString();
    }
}
