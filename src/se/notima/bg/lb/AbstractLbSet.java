package se.notima.bg.lb;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import se.notima.bg.BgFooter;
import se.notima.bg.BgHeader;
import se.notima.bg.BgRecord;
import se.notima.bg.BgSet;
import se.notima.bg.Transaction;

public abstract class AbstractLbSet implements BgSet {

	protected BgHeader	header;
	protected BgFooter	footer;
	protected Vector<Transaction>	records;
	protected Vector<Transaction> creditTransactions = new Vector<Transaction>();
	protected Map<String, Vector<BgRecord>>	creditRecords = new TreeMap<String, Vector<BgRecord>>();

	public AbstractLbSet(BgHeader header, BgFooter footer) {
		this.header = header;
		this.footer = footer;
		records = new Vector<Transaction>();
	}
	
	public String getSenderBankAccount() {
		return(header.getSenderAccount());
	}

    public String getRecipientBankAccount() {
        return(null);
    }

	public String getCurrency() {
		return(header.getCurrency());
	}
	
	public void setCurrency(String currency) {
		header.setCurrency(currency);
	}
	
	public java.util.Date getPayDate() {
		return(header.getPayDate());
	}
	
	public BgHeader getHeader() {
		return header;
	}
	
	public void setHeader(BgHeader header) {
		this.header = header;
	}
	
	public BgFooter getFooter() {
		return(footer);
	}
	
	public void setFooter(BgFooter footer) {
		this.footer = footer;
	}
	
	public Vector<Transaction> getRecords() {
		return records;
	}
	
	/**
	 * Adds a payment to the set
	 * @param payment
	 */
    public void addTransaction(Transaction payment) {
    	if (footer!=null) {
    		footer.incrementAmount(payment.getAmount());
    		footer.incrementCount();
    	}
    	if (payment.getAmount()<0) {
    		creditTransactions.add(payment);
    	}
        records.add((LbPayment)payment);
    }

	
	public void setRecords(Vector<Transaction> records) {
		this.records = records;
		footer.setAmount(0.0);
		if (records!=null) {
			footer.setCount(records.size());
		}
		for (Iterator<Transaction> it = records.iterator(); it.hasNext();) {
			footer.incrementAmount(it.next().getAmount());
		}
	}

	public String toRecordString() {
		StringBuffer lines = new StringBuffer();
		lines.append(header.toRecordString() + "\n");
		Transaction payment;
		if (records!=null && records.size()>0) {
			for (int i=0; i<records.size(); i++) {
				payment = records.get(i);
				payment.setSeqNo(i+1);
				lines.append(payment.toRecordString());
			}
		}
		
		lines.append(footer.toRecordString() + "\n");
		
		return(lines.toString());
	}
	
	public abstract java.util.Date getCreditRecordDate(String recipientBg, double amount);	
	
	public abstract void addCreditRecord(BgRecord creditRecord);	

}
