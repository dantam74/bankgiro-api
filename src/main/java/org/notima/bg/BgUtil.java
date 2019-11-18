/** ===================================================================
	Bankgiro Java API
    
    Copyright (C) 2009  Daniel Tamm
                        Notima Consulting Group Ltd

    This API-library is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This API-library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this API-library.  If not, see <http://www.gnu.org/licenses/>.

    =================================================================== */

package org.notima.bg;

import java.text.*;
import java.util.Calendar;


/**
 *
 * @author Daniel Tamm
 */
public class BgUtil {

	private static SimpleDateFormat	dateFormat = new SimpleDateFormat("yyMMdd");


	/**
	 * Method for converting date to standard date format for BG-files
	 * If the date is null it equals to immediate payment.
	 *
	 * @param d		Date to be converted.
	 * @return		A date in BG-format. If null, GENAST is returned.
	 */
	public static String getDateString(java.util.Date d) {
		if (d==null) return("GENAST");
		return(dateFormat.format(d));
	}

	/**
	 * Return the date. If the date string is empty or "GENAST", null is returned.
	 * @param dateStr	Parses date format from BG.
	 * @return			A java date.
	 * @throws ParseException if the string can't be parsed.
	 */
	public static java.util.Date parseDateString(String dateStr) throws ParseException {
		if (dateStr==null || dateStr.trim().length()==0 
			|| "GENAST".equalsIgnoreCase(dateStr)
			|| "000000".equals(dateStr)) {
			return(null);
		}
		return(dateFormat.parse(dateStr));
	}
	
	/**
	 * Remove all non digit characters
	 *
	 * @param cleanUp		String to be cleaned.
	 * @return				A cleaned string.
	 */
	public static String toDigitsOnly(String cleanUp) {
		if (cleanUp==null) return("");
		StringBuffer buf = new StringBuffer();
		char c;
		for (int i=0; i<cleanUp.length();i++) {
			c = cleanUp.charAt(i);
			if (c>='0' && c<='9') {
				buf.append(c);
			}
		}
		return(buf.toString());
	}

    public static boolean hasDigitsOnly(String ref) {
        if (ref==null || ref.trim().length()==0) return(false);
        char c;
        for (int i=0; i<ref.length(); i++) {
            c = ref.charAt(i);
            if (!(c>='0' && c<='9')) {
                return(false);
            }
        }
        return true;
    }

    /**
     * Remove blanks from a string. Good for purging for instance IBAN numbers
     * 
     * @param cleanUp		String to clean up.
     * @return				A string without blanks.
     */
    public static String removeBlanks(String cleanUp) {
    	if (cleanUp==null) return null;
    	StringBuffer result = new StringBuffer();
    	char c;
    	for (int i=0;i<cleanUp.length(); i++) {
    		c = cleanUp.charAt(i);
    		if (c==' ' || c=='\t' || c=='\n' || c=='\r')
    			continue;
    		result.append(c);
    	}
    	return result.toString();
    }
    
    
	public static String trimLeadingZeros(String cleanUp) {
		if (cleanUp==null) return("");
		cleanUp = cleanUp.trim();
		if (cleanUp.length()==0) return("");
		int i=0;
		while(i<cleanUp.length() && (cleanUp.charAt(i))=='0') {
			i++;
		}
		return(cleanUp.substring(i));
	}
	
	/**
	 * Perform very basic validation (length checks)
	 * 
	 * @param swift			Swift to be validated.
	 * @param iban			Iban to be validated.
	 * @return
	 */
	public static boolean validateIban(String swift, String iban) {
		boolean pass = false;
		if (swift!=null && swift.trim().length()>0 &&
				iban!=null && iban.trim().length()>0) {
			pass = true;
		}
		return(pass);	
	}
	
	/**
	 *  Validate Bankgiro
	 *  @param Bankgiro			A string containing the bankgiro to be validated.
	 *  @return true if the bankgiro has passed length test.
	 */
	public static boolean validateBankgiro (String Bankgiro)
	{
		int length = BgUtil.toDigitsOnly(Bankgiro).length();
		if (length > 0 && length < 10)
			return true;
		return false;
	}   //  validateBankgiro

	/**
	 * Format bankgiro
	 * Formats a sequence of digits to the general form of a bankgiro account.
	 * If it's not a bg account no formatting occurs, the string is returned as is. 
	 * (ie \d{3,4}-\d{4})
	 */
	public static String formatBg(String digits) {
		String fmt = toDigitsOnly(digits);
		fmt = trimLeadingZeros(fmt);
		if (fmt.length()<7 || fmt.length()>8) return(digits);
		if (fmt.length()==8) {
			return(fmt.substring(0,4) + "-" + fmt.substring(4));
		} else {
			return(fmt.substring(0,3) + "-" + fmt.substring(3));
		}
	}
	
	/**
	 * Format postgiro
	 * Formats a sequence of digits to the general form of a postgiro account.
	 */
	public static String formatPg(String digits) {
		String fmt = toDigitsOnly(digits);
		fmt = trimLeadingZeros(fmt);
		if (fmt.length()<2) {
			return(fmt);
		} else {
			return(fmt.substring(0,fmt.length()-1) + "-" + fmt.substring(fmt.length()-1, fmt.length()));
		}
	}
	
	/**
	 * Formats taxId to BGMax format (10 digits)
	 */
	public static String formatTaxId(String taxId) throws ParseException {
		
		// Convert to digits only
		String taxDigits = toDigitsOnly(taxId);
		if (taxDigits.length()>12) {
			throw new ParseException(taxId + " invalid.", 0);
		}
		if (taxDigits.length()==12) {
			taxDigits = (String) taxDigits.subSequence(2, taxDigits.length()-1);
		}
		
		return taxDigits;
	}
	
	
	/**
	 * Converts a double to 12 digits where the two last digits
	 * are "öre" or "cents".
	 * Negative amounts are returned as absolute (no negative indicator)
	 *
	 * @return		A string representation of an amount in BG-format.
	 */
	public static String getAmountStr(double amount) {
		return(getAmountStr(amount, 12, true));
	}
	
	/**
	 * 
	 * @param amount	The amount to be converted.
	 * @param len		Let you specify the length of the string
	 * @param absolute	If true no negative indicator. If false the last digit is replaced
	 * 					with a letter according to a specific pattern to indicate a negative
	 * 					value.
	 * @return			The amount in string representation
	 */
	public static String getAmountStr(double amount, int len, boolean absolute) {
		StringBuffer buf = new StringBuffer();
		long newAmount = Math.round(Math.abs(amount)*100.0);
		buf.append(Long.toString(newAmount));
		while(buf.length()<len) {
			buf.insert(0, "0");
		}
		// Replace if negative and not absolute
		if (amount<0 && !absolute) {
			String minusStr;
			char lastDigit = buf.charAt(buf.length()-1);
			switch (lastDigit) {
				case '0' : minusStr = "-"; break;
				case '1' : minusStr = "J"; break;
				case '2' : minusStr = "K"; break;
				case '3' : minusStr = "L"; break;
				case '4' : minusStr = "M"; break;
				case '5' : minusStr = "N"; break;
				case '6' : minusStr = "O"; break;
				case '7' : minusStr = "P"; break;
				case '8' : minusStr = "Q"; break;
				case '9' : minusStr = "R"; break;
				default : minusStr = "E"; break; // Illegal character, shouldn't happen
			}
			// Replace last digit
			buf.replace(buf.length()-1, buf.length(), minusStr);
		}
		return(buf.toString());
	}
	
	public static double parseAmountStr(String amountStr) {
		Double amount = Double.parseDouble(amountStr);
		amount = amount / 100.0;
		return(amount);
	}

	/**
	 * Return Luhn Digit using an amount as in data
	 * 
	 * @param amount		The amount
	 * @return				The luhn digit associated with the amount.
	 */
	public static int getLuhnDigit(double amount) {
		String amountStr = trimLeadingZeros(getAmountStr(amount,20,true));
		return(getLuhnDigit(amountStr));
	}
	
	/**
	 * Luhn algoritm.
	 * @param indata		The numbers to calculated a Luhn digit from.
	 * @return	The luhn digit.
	 */
	public static int getLuhnDigit(String indata) {
		int a = 2;
		int sum = 0;
		int term;
		for (int i = indata.length() - 1; i >= 0; i--) {
		  term = Character.digit(indata.charAt(i),10) * a;
		  if ( term > 9) term -= 9;
		  sum += term;
		  a = 3 - a;
		}
		int tens = sum/10*10;
		int subtractfrom = tens+10;
		return((subtractfrom - sum)%10);
	}

	/**
	 * Converts a string of digits to the equivalent OCR-number
	 * @param indata		The data to be converted
	 * @return				An OCR number with a checkdigit.
	 */
	public static String toOCRNumber(String indata) {
		indata = toDigitsOnly(indata);
		return(indata + getLuhnDigit(indata));
    }
	
	/**
	 * Converts a string of digits to an OCR-number with length check
	 * @param indata		Numbers to be converted.
	 * @return				OCR-number with luhn digit and length check.
	 */
	public static String toOCRNumberWithLengthCheck(String indata) {
		indata = toDigitsOnly(indata);
		int length = indata.length()+2; // Add two for the check digits
		length = length%10; // Modulus
		String ocrCode = indata + length;
		return(ocrCode + getLuhnDigit(ocrCode));
	}
	
	/**
	 *  Validate OCR number.
	 *  - Based on LUHN formula (Modulus10)
	 *  @param OCR
	 *  @return True if OCR is correct
	 */
	public static boolean isValidOCRNumber (String OCR)
	{
		if (OCR == null || OCR.length() == 0)
			return false;

		/**
		 *  1:  Double the value of alternate digits beginning with
		 *      the	first right-hand digit (low order).
		 *  2:  Add the individual digits comprising the products
		 *      obtained in step 1 to each of the unaffected digits
		 *      in the original number.
		 *  3:  Subtract the total obtained in step 2 from the next higher
		 *      number ending in 0 [this in the equivalent of calculating
		 *      the "tens complement" of the low order digit (unit digit)
		 *      of the total].
		 *      If the total obtained in step 2 is a number ending in zero
		 *      (30, 40 etc.), the check digit is 0.
		 *  Example:
		 *  Account number: 4992 73 9871 6
		 *
		 *  4  9  9  2  7  3  9  8  7  1  6
		 *    x2    x2    x2    x2    x2
		 *  -------------------------------
		 *  4 18  9  4  7  6  9 16  7  2  6
		 *
		 *  4 + 1 + 8 + 9 + 4 + 7 + 6 + 9 + 1 + 6 + 7 + 2 + 6 = 70
		 *  70 % 10 = 0
		 */

		//  Clean up number
		String ccNumber1 = toDigitsOnly(OCR);
		int ccLength = ccNumber1.length();
		//  Reverse string
		StringBuffer buf = new StringBuffer();
		for (int i = ccLength; i != 0; i--)
			buf.append(ccNumber1.charAt(i-1));
		String ccNumber = buf.toString();

		int sum = 0;
		for (int i = 0; i < ccLength; i++)
		{
			int digit = Character.getNumericValue(ccNumber.charAt(i));
			if (i % 2 == 1)
			{
				digit *= 2;
				if (digit > 9)
					digit -= 9;
			}
			sum += digit;
		}

		if (sum % 10 == 0)
			return true;

		return false;
	}   //  validateOCRNumber

	/**
	 * Fills to a specified length
	 * 
	 * @param		str			The string to be filled. If null it will be filled only with fillChar.
	 * @param		rightAlign	If the string should be right aligned.
	 * @param		fillChar	The char to fill.
	 * @param		len			Lenght to fill. If str is more than len, str is truncated.
	 */
	public static String fillToLength(String str, boolean rightAlign, char fillChar, int len) {
		if (str==null) str = "";
		StringBuffer buf = new StringBuffer();
		if (str.length()>len) {
			if (rightAlign)
				buf.append(str.substring(str.length()-len, str.length()));
			else
				buf.append(str.substring(0, len));
			return(buf.toString());
		}
		// Append
		buf.append(str);
		while(buf.length()<len) {
			if (rightAlign) {
				buf.insert(0, fillChar);
			} else {
				buf.append(fillChar);
			}
		}
		return(buf.toString());
	}

	public static boolean validateBankAccount(String clearing, String accountNo) {
		if (clearing==null || accountNo==null) return false;
		String clr = BgUtil.toDigitsOnly(clearing);
		String no = BgUtil.toDigitsOnly(accountNo);
		if (clr.length()<4 || clr.length()>5) {
			return false;
		}
		if (no.length()<5 || clr.length()>10) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param clearing		Routing number (clearing)
	 * @param accountNo		Account number
	 * @return Trims account from all non digits. Separates the clearing and account no
	 * 		   with a hyphen. '-'
	 */
	public static String getAccountString(String clearing, String accountNo) {
		String clr = BgUtil.toDigitsOnly(clearing);
		String no = BgUtil.toDigitsOnly(accountNo);
		return(clr + "-" + no);
	}

	/**
	 * Removes all non USASCII chars and converts to uppercase. Swedish characters
	 * ÅÄÖ are replaced with A and O.
	 * @param clearupstring
	 * @return	A string containing only ASCII
	 */
	public static String onlyUSASCII(String clearupstring) {
		if (clearupstring==null) return null;
		StringBuffer buf = new StringBuffer();
		String work = clearupstring.toUpperCase();
		char c;
		for (int i=0; i<work.length(); i++) {
			c = work.charAt(i);
			if (c=='Å' || c=='Ä') {
				buf.append("A");
				continue;
			}
			if (c=='Ö') {
				buf.append("O");
				continue;
			}
			if ((c>='0' && c<='9') || (c>='A' && c<='Z') || c==',' || c=='.' || c=='-') {
				buf.append(c);
			} else {
				// Blank out non-legal chars
				buf.append(" ");
			}
				
		}
		return(buf.toString());
	}

	/**
	 * Returns number of days from today
	 */
	public static int daysFromNow(java.util.Date date) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		long then = date.getTime();
		
		Calendar nowCal = Calendar.getInstance();
		nowCal.set(Calendar.HOUR_OF_DAY, 0);
		nowCal.set(Calendar.MINUTE, 0);
		nowCal.set(Calendar.SECOND, 0);
		nowCal.set(Calendar.MILLISECOND, 0);
		
		long now = nowCal.getTimeInMillis();
		long daysInMillis = then - now;
		long days = daysInMillis / 1000 / 60 / 60 / 24;
		
		return ((int)days);
	}
	
	/**
	 * Adds number of days on specified date
	 * 
	 */
	public static java.util.Date addDays(java.util.Date date, int daysToAdd) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.DATE, daysToAdd);
		
		return(cal.getTime());
		
	}
	
}
