import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements the backtracking search with forward checking. 
 */
public class ForwardCheckingCSPSolver extends BacktrackingCSPSolver {
	
	/**
	 * Implements the actual forward checking. Infers the values to be deleted
	 * from the domains of some variables based on the given variable and value.
	 */
	@Override
	protected <E> Inference<E> inference(CSP<E> csp, Assignment<E> assignment, String var, E value) {
		//Assignment<E> copy=new Assignment<>();
		//copy.putAll(assignment);
		Inference<E> inference1=new Inference<>();
		for(int i=0;i<csp.constraints.size();i++){
			Constraint constraint=csp.constraints.get(i);
			List<String> list=constraint.getScope();
			if(list.indexOf(var)!=-1&&(!assignment.containsKey(list.get(0))||!assignment.containsKey(list.get(1)))){
				if(orderDomainValues(csp,list.get(0),assignment).isEmpty()||orderDomainValues(csp,list.get(1),assignment).isEmpty()){
					return null;
				}
				Set<E> set=new HashSet<>();
				set.add(value);
				inference1.put(list.get(1),set);
				//int link=orderDomainValues(csp,var,assignment).indexOf(value);
				//System.out.println("link "+link);
				//if(link!=-1){
				//}
			}
		}
		return inference1;
		/* TODO
		 * Implement the forward checking. You may want to iterate over all
		 * constraints to identify those who involve the given variable. Then,
		 * iterate over the variables of the scope of the constraint and check
		 * if the variable is not yet assigned. If it is not assigned, check all
		 * the values of the domain of that variable, and identify those values
		 * that are inconsistent with the constraint (therefore, you might temporarily
		 * modify the assignment with the value to test, and restore the assignment
		 * later on). The inconsistent values should be added to the inference that
		 * will be returned. If no value was found at all, then return failure (null in this
		 * case).
		 */
	}
}
