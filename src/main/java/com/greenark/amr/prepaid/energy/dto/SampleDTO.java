package com.greenark.amr.prepaid.energy.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SampleDTO implements Serializable 
{
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private String name;
}
