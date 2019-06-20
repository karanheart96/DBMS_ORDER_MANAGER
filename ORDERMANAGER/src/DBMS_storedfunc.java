import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBMS_storedfunc {
  /**
  This function checks whether the country is valid or not.
  Reads data from a tab separated text file containing  the names of countries.
   */
  public static boolean isCountry(String country) {
    country = country + " ";
    String filename = "data/Countries.txt";
    List<String> country_list = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");
        String valid_country = data[0];
        valid_country = valid_country.toUpperCase();
        country_list.add(valid_country);
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
    }
    for(String str : country_list) {
      if(str.equalsIgnoreCase(country)) {
        return true;
      }
    }
    return false;
  }

  /**
   *  This function checks whether the state is valid US state or not.
   *  Reads data from a tab separated text file containing the names of states.
   *  Cannot determine validity of non-US states
   *
   * @param state the state
   * @param country the country
   */
  public static boolean isState(String state, String country) {
    if(!country.equalsIgnoreCase("United States")) {
      // if not US, then assume state field is true
      return true;
    }
    String filename = "data/us_states.txt";
    List<String> state_list = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");
        String valid_state = data[0];
        valid_state = valid_state.toUpperCase();
        state_list.add(valid_state);
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
    }
    for(String str : state_list) {
      if(str.equalsIgnoreCase(state)) {
        return true;
      }
    }

    return false;
  }

  /**
  This function checks whether the PIN is valid or not.
   */
  public static boolean isPin(int pin) {
    if(pin > 1 || pin < 99999) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
  This function checks whether the no of units is valid or not.
  Checks for positive quantity.
   */
  public static boolean isUnit(int no_of_units) {
    if(no_of_units >= 0){
      return true;
    }
    else {
      return false;
    }
  }

  /**
  This function checks whether the price is valid or not.
  Checks for positive quantity.
   */
  public static boolean isPrice(BigDecimal price) {
    double d = price.doubleValue();
    if(d >= 0.00) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Checks whether or not the given zip code is a valid US zip code.
   * Will not verify zip codes of other countries.
   *
   * @param zipCode the zipcode
   * @param country the country
   * @return true if valid zipcode, false otherwise
   */
  public static boolean isZipCode(String zipCode, String country) {
    if(!country.equalsIgnoreCase("United States")) {
      // if not US country, then just assumes zip code is correct
      return true;
    }
    String regex = "^[0-9]{5}(?:-[0-9]{4})?$";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(zipCode);

    return matcher.matches();
  }

  /**
   * This function determines if the SKU follows the form AA-NNNNNN-CC,
   * where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an uppper case letter.
   * For example, "AB-123456-0N".
   *
   * @param sku the SKU
   * @return true if SKU is valid SKU, false otherwise
   */
  public static boolean isSKU(String sku) {
    if (sku.matches("([\\dA-Z]{2})-(\\d{6})-([\\d0-9A-Z]{2})$")) {
      return true;
    }
    return false;
  }

}
