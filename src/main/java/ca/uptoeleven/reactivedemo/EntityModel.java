package ca.uptoeleven.reactivedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityModel {
	private int id;
	private boolean isOdd;
}
