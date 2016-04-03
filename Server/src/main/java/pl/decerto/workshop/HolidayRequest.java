package pl.decerto.workshop;

import java.util.UUID;
import org.springframework.data.annotation.Id;

public class HolidayRequest {

	@Id
	private String uuid = UUID.randomUUID().toString();

	private String id;

	private String number;

	private String name = "Maciej";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HolidayRequest{");
		sb.append("number='").append(number).append('\'');
		sb.append(", uuid='").append(uuid).append('\'');
		sb.append(", id='").append(id).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}
}

