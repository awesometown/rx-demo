package ca.uptoeleven.reactivedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult {
	private List<EntityModel> models;
	private long elapsedTime;
}
