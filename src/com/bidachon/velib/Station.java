package com.bidachon.velib;

public class Station {
	
	public class Location {
		public Location(Double lat, Double lng) 
		{
			_lat = lat;
			_lng = lng;
		}
		public Double latitude() {return _lat;}
		public Double longitude() {return _lng;}
		private Double _lat;
		private Double _lng;
		
		
	}
	
	@Override
	public String toString() {
		return "Station [_name=" + _name + ", _number=" + _number
				+ ", _address=" + _address + ", _fullAddress=" + _fullAddress
				+ ", _status=" + _status + ", _location=" + _location
				+ ", _distance=" + _distance + ", bikes=" + _bikes
				+ ", stands=" + _stands + ", total=" + total + ", ticket=" + ticket
				+ "]";
	}
	//getter
	public Station() {}
	public String name() {return _name;}
	public Integer number() {return _number;}
	public String address() {return _address;}
	public String fullAddress() {return _fullAddress;}
	public Boolean status() {return _status;}
	public Location location() {return _location;}
	public Integer distance() {return _distance;}
	public void setDistanceFromCurrent(Integer dist)
	{
		_distance = dist;
	}

	//setter
	public void setName(String s) { _name = s;}
	public void setNumber(Integer i) { _number = i;}
	public void setAddress(String s) { _address = s;}
	public void setFullAddress(String s) { _fullAddress = s;}
	public void setStatus(Boolean b) { _status = b;}
	public void setLocation(Location l) { _location = l;}
	
	private String _name;
	private Integer _number;
	private String _address;
	private String _fullAddress;
	private Boolean _status;
	private Location _location;
	private Integer _distance = 0;
	private Integer _bikes = 0;
	private Integer _stands = 0;
	private Integer total = 0;
	private Integer ticket = 0;
	private Boolean favorite = false;
	public Boolean getFavorite() {
		return favorite;
	}
	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}
	public Integer getBikes() {
		return _bikes;
	}
	public void setBikes(Integer bikes) {
		this._bikes = bikes;
	}
	public Integer getStands() {
		return _stands;
	}
	public void setStands(Integer stands) {
		this._stands = stands;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getTicket() {
		return ticket;
	}
	public void setTicket(Integer ticket) {
		this.ticket = ticket;
	}
	
	
}
