package zty.sdk.model;

public class ActivateResult {

    private String loginUrl;
    private String registerUrl;
    private String changePasswordUrl;
    private String payways;
    private String alipayWapUrl;
    private String adfd;
    private String dipcon;
    private String dipcon2;
    private String dipurl;
    private String noturl;
    private String exiturl;
    private String identi1;
    private String identi2;
    private String paywaysign;
    private String serverUrl;
    
    public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getPaywaysign() {
		return paywaysign;
	}

	public void setPaywaysign(String paywaysign) {
		this.paywaysign = paywaysign;
	}

	public String getIdenti1() {
		return identi1;
	}

	public void setIdenti1(String identi1) {
		this.identi1 = identi1;
	}

	public String getIdenti2() {
		return identi2;
	}

	public void setIdenti2(String identi2) {
		this.identi2 = identi2;
	}

	public void setChangePasswordUrl(String changePasswordUrl) {
		this.changePasswordUrl = changePasswordUrl;
	}

	private String mkurl;
    private String mk;
    
    public String getMk() {
        return mk;
    }

    public void setMk(String mk) {
        this.mk = mk;
    }
    
    public String getMkurl() {
        return mkurl;
    }

    public void setMkurl(String mkurl) {
        this.mkurl = mkurl;
    }
    
    
    public String getExiturl() {
        return exiturl;
    }

    public void setExiturl(String exiturl) {
        this.exiturl = exiturl;
    }
    
    public String getNoturl() {
        return noturl;
    }

    public void setNoturl(String noturl) {
        this.noturl = noturl;
    }
    
   
    
    public String getDipurl() {
        return dipurl;
    }

    public void setDipurl(String dipurl) {
        this.dipurl = dipurl;
    }
    
    public String getDipcon2() {
        return dipcon2;
    }

    public void setDipcon2(String dipcon2) {
        this.dipcon2 = dipcon2;
    }
    
    public String getDipcon() {
        return dipcon;
    }

    public void setDipcon(String dipcon) {
        this.dipcon = dipcon;
    }
    
    public String getAdfd() {
        return adfd;
    }

    public void setAdfd(String adfd) {
        this.adfd = adfd;
    }
    
    public ActivateResult() {
    }

    public String getChangePasswordUrl() {
        return changePasswordUrl;
    }

    public void setChangePassword(String changePasswordUrl) {
        this.changePasswordUrl = changePasswordUrl;
    }
    
    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getPayways() {
        return payways;
    }

    public void setPayways(String payways) {
        this.payways = payways;
    }

    public String getAlipayWapUrl() {
        return alipayWapUrl;
    }

    public void setAlipayWapUrl(String alipayWapUrl) {
        this.alipayWapUrl = alipayWapUrl;
    }

	@Override
	public String toString() {
		return "ActivateResult [loginUrl=" + loginUrl + ", registerUrl="
				+ registerUrl + ", changePasswordUrl=" + changePasswordUrl
				+ ", payways=" + payways + ", alipayWapUrl=" + alipayWapUrl
				+ ", adfd=" + adfd + ", dipcon=" + dipcon
				+ ", dipcon2=" + dipcon2 + ", dipurl=" + dipurl + ", noturl="
				+ noturl + ", exiturl=" + exiturl + ", ident1=" + identi1
				+ ", identi2=" + identi2 + ", mkurl=" + mkurl + ", mk=" + mk
				+ "]";
	}

   

}
