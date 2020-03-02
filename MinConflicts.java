import java.util.*;

/**
 * Implements the min-conflict local search CSP solver 
 */
public class MinConflicts implements CSPSolver {

	@Override
	public <E> CSPResult<E> solve(CSP<E> csp) {
		/* TODO
		 * find reasonable value for max iterations.
		 */
		return minConflicts(csp, 2000);
	}
	
	/**
	 * Implements the min-conflicts algorithm.
	 * @param csp The csp to solve
	 * @param maxSteps The max number of steps
	 * @return A solution to the csp, or null if none was found.
	 */
	private static <E> CSPResult<E> minConflicts(CSP<E> csp, int maxSteps) {
		Assignment<E> assignment=createCompleteAssignment(csp);
		for (int i=0;i<maxSteps;i++){
			int flag=0;
			Set<String> set=new HashSet<>();
			for (int j=0;j<csp.constraints.size();j++){
				List<String> list=csp.constraints.get(j).getScope();
				if (assignment.get(list.get(0))==assignment.get(list.get(1))){
					flag=1;
					set.add(list.get(0));
					set.add(list.get(1));
				}
			}
			//System.out.println(set);
			if (flag==0){
				return new CSPResult<E>(assignment, i);
			}
			else {
				String var=getRandomConflictedVariable(set,assignment,csp);
				int min=Integer.MAX_VALUE;
				int mark=0;
				for (int j=0;j<csp.domains.get(var).size();j++){
					int numconflicts=conflicts(var,csp.domains.get(var).get(j),assignment,csp);
					if (min>numconflicts){
						mark=j;
						min=numconflicts;
					}
					if (min==numconflicts){
						double r=Math.random();
						if (r<0.5){
							mark=j;
							min=numconflicts;
						}
					}
				}
				assignment.put(var,csp.domains.get(var).get(mark));
			}
		}
		/* TODO
		 * The implementation can pretty much follow the pseudo code
		 * in the text book:
		 * 
		 * current <- an initial complete assignment for csp
		 * for i = 1 to max_steps do
		 *   if current is a solution for csp then return current
		 *   var <- a randomly chosen conflicted variable from csp.VARIABLES
		 *   value <- the value v for var that minimizes CONFLICTS(var, v, current, csp)
		 *   set var = value in current
		 * return failure
		 * 
		 * Most of it is straight forward, because there is a separate
		 * function for it. Only finding the value that minimizes the
		 * number of conflicts is a little more complicated. However,
		 * you should use the function conflicts() for this purpose.
		 * Also, please note that "failure" is "null" in this implementation.
		 * You should return the result like "new CSPResult(current, i);"
		 */
		return null;
	}
	
	/**
	 * Randomly selects a conflicted variable.
	 * @param current The current assignment
	 * @param csp The csp
	 * @return A randomly chosen conflicted variable.
	 */
	private static <E> String getRandomConflictedVariable(Set<String> set,Assignment<E> current, CSP<E> csp) {
//		Set<String> set=new HashSet<>();
//		for (int i=0;i<csp.constraints.size();i++){
//			List<String> list=csp.constraints.get(i).getScope();
//			if (current.get(list.get(0))!=null&&current.get(list.get(1))!=null){
//				if (current.get(list.get(0))==current.get(list.get(1))){
//					set.add(list.get(0));
//					set.add(list.get(1));
//				}
//
//			}
//		}
		/* TODO
		 * First, you should create an initially empty set of conflicted variables.
		 * Then, iterate over all constraints, and if it is not consistent, add
		 * all the variables from its scope to the set of conflicted variables.
		 * Afterwards, just return a randomly selected one of them. (Hint: make
		 * sure that variables that appear in multiple constraints are not
		 * selected with a higher probability, they should be selected unbiased).
		 */
		Iterator<String> it=set.iterator();
		int r=(int)(Math.random()*set.size());
		int mark=0;
		String var="";
		while (it.hasNext()){
			String next=it.next();
			var=next;
			if (mark>=r){
				break;
			}
			mark++;
		}
		return var;
	}
	
	/**
	 * Creates an assignment in which every varibale is assigned a value from its domain.
	 * @param csp The underlying csp that defines the domains and the variables
	 * @return A complete assignment
	 */
	private static <E> Assignment<E> createCompleteAssignment(CSP<E> csp) {
		/* TODO
		 * create a new assignment and randomly assign a value to every
		 * variable from its domain.
		 */
		Assignment<E> assignment=new Assignment<>();
		for (int i=0;i<csp.variables.size();i++){
			String key=csp.variables.get(i);
			int r=(int)(Math.random()*csp.domains.get(key).size());
			assignment.put(key,csp.domains.get(key).get(r));
		}
		return assignment;
	}
	
	/**
	 * Computes the number of conflict based on an assignment, but with one variable
	 * set to a specific value.
	 * @param var The variable to be checked
	 * @param value The value to be checked
	 * @param current The current assignment used as basis
	 * @param csp The csp problem
	 * @return The number of conflict given the current assignment, but with var=value
	 */
	private static <E> int conflicts(String var, E value, Assignment<E> current, CSP<E> csp) {
		Assignment<E> copy=new Assignment<>();
		copy.putAll(current);
		current.put(var,value);
		int numconflicts=0;
		for (int i=0;i<csp.constraints.size();i++){
			List<String> list=csp.constraints.get(i).getScope();
			if (current.get(list.get(0))!=null&&current.get(list.get(1))!=null){
				if (current.get(list.get(0))==current.get(list.get(1))){
					numconflicts++;
				}
			}
		}
		/* TODO
		 * You might want to temporarily modify the assignment
		 * to set var = value (undo this afterwards!). Then
		 * iterate over all constraints and count the number of
		 * insonsistent constraints.
		 */
		current.putAll(copy);
		return numconflicts;
	}
}