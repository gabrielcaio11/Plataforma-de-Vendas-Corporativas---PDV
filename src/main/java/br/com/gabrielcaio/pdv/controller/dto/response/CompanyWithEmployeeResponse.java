package br.com.gabrielcaio.pdv.controller.dto.response;

import java.util.List;

public record CompanyWithEmployeeResponse(
    Long id, String nameCompany, List<EmployeeResponse> employees) {}
