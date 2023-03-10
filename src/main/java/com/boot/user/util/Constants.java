package com.boot.user.util;

public class Constants {

	private Constants(){
	}

	public static final String GET_PRODUCT_BY_PRODUCT_NAME = "/?productName={productName}";

	public static final String GET_ALL_PRODUCTS_FOR_USER = "/?productNames={products}&includeInactive={includeInactive}";

	public static final String DELETE_CART_BY_EMAIL =  "/?email={email}";

	public static final String CONFIRM_USER_ACCOUNT = "/confirm/";

	public static final String PASSWORD_RESET_EMAIL = "/change/";

	//Regular expression used for email validation
	public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

	public static final String USER_ID_HEADER = "User-Id";
}
