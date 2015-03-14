package test.tw.me.ychuang.rpc;

import java.util.Date;

public class User {

	private long id;

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String introduction;

	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	private Date clientSideTime;

	public Date getClientSideTime() {
		return this.clientSideTime;
	}

	public void setClientSideTime(Date clientSideTime) {
		this.clientSideTime = clientSideTime;
	}

	private Date serverSideTime;

	public Date getServerSideTime() {
		return this.serverSideTime;
	}

	public void setServerSideTime(Date serverSideTime) {
		this.serverSideTime = serverSideTime;
	}
}
