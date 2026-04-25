package com.syos.usecases.reports;

import com.syos.entities.Bill;
import com.syos.usecases.repositories.BillRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Bill report showing all customer transactions.
 * Requirement: "Bill report. This would contain all the customer transactions
 * that have
 * taken place in the SYOS system."
 */
public class BillReport extends ReportTemplate {
    private final BillRepository billRepository;
    private final Bill.TransactionType transactionType;

    public BillReport(BillRepository billRepository, Bill.TransactionType transactionType) {
        this.billRepository = billRepository;
        this.transactionType = transactionType;
    }

    @Override
    protected String getReportHeader() {
        return String.format("=== BILL REPORT ===\nTransaction Type: %s\n",
                transactionType == null ? "ALL" : transactionType);
    }

    @Override
    protected String getReportBody() {
        List<Bill> bills = transactionType == null
                ? billRepository.findAll()
                : billRepository.findByTransactionType(transactionType);

        if (bills.isEmpty()) {
            return "No bills found.";
        }

        StringBuilder body = new StringBuilder();
        body.append(String.format("%-10s %-20s %-15s %-12s %-12s %-12s %-15s\n",
                "Bill No.", "Date & Time", "Type", "Subtotal", "Discount", "Total", "Customer ID"));
        body.append("-".repeat(110)).append("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        double totalSales = 0.0;

        for (Bill bill : bills) {
            body.append(String.format("%-10d %-20s %-15s %-12.2f %-12.2f %-12.2f %-15s\n",
                    bill.getSerialNumber(),
                    bill.getBillDate().format(formatter),
                    bill.getTransactionType(),
                    bill.getSubtotal(),
                    bill.getDiscount(),
                    bill.getTotal(),
                    bill.getCustomerId() != null ? bill.getCustomerId() : "N/A"));

            totalSales += bill.getTotal();
        }

        body.append("-".repeat(110)).append("\n");
        body.append(String.format("Total Bills: %d\n", bills.size()));
        body.append(String.format("Total Sales: Rs. %.2f\n", totalSales));

        return body.toString();
    }
}
