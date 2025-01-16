package run.mone.m78.service.bo.statistics;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@ToString
public class Department {
    private String name;
    private int tier;// 部门级别1234
    private Department parent;
    private List<Department> subDepartments = new CopyOnWriteArrayList<>();
    private List<Employee> employees = new CopyOnWriteArrayList<>();
}
