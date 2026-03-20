package PageObject;

public interface LoginPageElements {
    String LoginText = "//input[@type='submit' and @name='continue' and @value='Click Here To Login']";
	    String VENDOR_ID = "//input[@name='vendor_id']";
	    String passwordField = "//input[@name='password']";
    String ROLE_DROPDOWN = "//select[@name='role']";
    String LOGIN_BUTTON = "//input[@name='continue' and @value='Click Here To Login']";
    String SelectLocation = "//ng-select[@placeholder='Select Location']";
    String SelectCategory = "//ng-select[@placeholder='Select Category']";
    String SelectCounsellor = "//ng-select[@placeholder='Select Counsellor']";

}
