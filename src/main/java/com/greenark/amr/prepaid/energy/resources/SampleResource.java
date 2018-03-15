package com.greenark.amr.prepaid.energy.resources;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
@Setter
@Getter
@JsonInclude(Include.ALWAYS)
public class SampleResource extends ResourceSupport
{
	private String name;
}
