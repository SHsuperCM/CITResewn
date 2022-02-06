package shcm.shsupercm.fabric.citresewn.pack.cit;

import java.util.List;

public abstract class CITType {
    public List<? extends CITCondition> modifyConditions(List<? extends CITCondition> conditions) {
        return conditions;
    }
}
