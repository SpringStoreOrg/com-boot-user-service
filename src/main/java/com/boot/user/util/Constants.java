package com.boot.user.util;

public class Constants {

	public static final String GET_PRODUCT_BY_PRODUCT_NAME = "/getProductByProductName?productName={productName}";

	public static final String DELETE_CART_BY_EMAIL =  "/deleteCartByEmail/{email}";

	public static final String CONFIRM_USER_ACCOUNT = "/confirmUserAccount/";

	//Regular expression used for email validation
	public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

	
}
