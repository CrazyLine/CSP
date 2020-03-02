import java.util.*;

public class AC3 {
	/**
	 * This class represents a single arc for the AC-3 algorithm.
	 */
	public static class Arc {
		private String value1, value2;

		public Arc(String value1, String value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
	}
	
	/**
	 * Implements the AC-3 algorithm to make a csp arc consistent.
	 * @param csp The csp
	 * @return Whether an inconsistency was found (false) or not (true)
	 * @throws Exception
	 */
	public static <E> boolean ac3(CSP<E> csp) throws Exception {
		Queue<Arc> queue=new LinkedList<>();
//		for (int i=0;i<csp.variables.size();i++){
//			for(int j=0;j<csp.variables.size();j++){
//				if(i!=j){
//					queue.offer(new Arc(csp.variables.get(i),csp.variables.get(j)));
//				}
//			}
//		}
		for (int i=0;i<csp.constraints.size();i++){
			List<String> list=csp.constraints.get(i).getScope();
			queue.offer(new Arc(list.get(0),list.get(1)));
			queue.offer(new Arc(list.get(1),list.get(0)));
		}
		while (!queue.isEmpty()){
			Arc arc=queue.poll();
			if(revise(csp,arc.value1,arc.value2)){
				if(csp.domains.get(arc.value1).size()==0){
					return false;
				}
				Set<String> set=neighbors(csp,arc.value1);
				Iterator<String> it=set.iterator();
				while (it.hasNext()){
					String next=it.next();
					queue.offer(new Arc(next,arc.value1));
				}
			}
		}
		/* TODO
		 * First, set up a queue of all arcs. For each constraint (you can assume that
		 * all constraints are binary constraints) add two arcs, one forward, and one
		 * backwards. Then implement the following (taken from text book):
		 * 
		 * while queue is not empty do
		 *   (Xi, Xj) <- REMOVE-FIRST(queue)
		 *   if REVISE(csp, Xi, Xj) then
		 *     if size of Di = 0 then return false
		 *     for each Xk in Xi.NEIGHBORS - {Xj} do
		 *       add (Xk, Xi) to queue
		 * return true
		 * 
		 * Note that Xi and Xj correspond to Arc.value1 and Arc.value2
		 * after some arc has been polled from the queue.
		 */
		return true;
	}
	
	/**
	 * Implements the revise-routine of the AC-3 algorithm. Effectively iterates
	 * over all domain values of var1 and checks if there is at least 1 possible value
	 * for var2 remaining. If not, removes that value from the domain of var1.
	 * @param csp
	 * @param var1
	 * @param var2
	 * @return
	 */
	private static <E> boolean revise(CSP<E> csp, String var1, String var2) {
		boolean flag=false;
		Set<Integer> set=new HashSet<>();
		if(csp.domains.get(var1)!=null) {
			for (int j = 0; j < csp.domains.get(var1).size(); j++) {
				Assignment<E> assignment = new Assignment<>();
				assignment.put(var1, csp.domains.get(var1).get(j));
				int mark = 0;
				for (int i = 0; i < csp.domains.get(var2).size(); i++) {
					assignment.put(var2, csp.domains.get(var2).get(i));
					if (!csp.isConsistent(assignment)) {
						mark++;
						set.add(j);
					}
				}
				//System.out.println("size: "+csp.domains.get(var2).size()+" mark: "+mark);
				if (csp.domains.get(var2).size() == mark) {
					flag = true;
				}
			}
			if (flag) {
				//System.out.println("flag is true!");
				Iterator<Integer> it = set.iterator();
				while (it.hasNext()) {
					int next = it.next();
					csp.domains.get(var1).remove(next);
				}
			}
		}

		/* TODO
		 * You may want to use a temporary Assignment to check whether a constraint
		 * is violated by any values for var1 and var2. Iterate over all domain values
		 * of var1. Then iterate over all domain values of var2 and prepare the
		 * temporary assignment accordingly. If all values for var2 produce an
		 * inconsistent assignment, remove the current value from the domain of
		 * var1. Hint: You cannot modify the domain as long as you are iterating over
		 * it, therefore I recommend to temporarily store the values to be deleted in
		 * a list or something, and then delete them all together after you iterated
		 * over all domain values. Also, don't forget to return whether you actually
		 * modified the domain of var1. 
		 */
		return flag;
	}
	
	/**
	 * Computes the "neighbors" of a variable in a CSP. A variable is
	 * a neighbor if it is coupled to another variable by a constraint.
	 * @param csp The csp
	 * @param var The variable the neighbors of which are to be found.
	 * @return The neighbors of the given variable.
	 */
	private static Set<String> neighbors(CSP<?> csp, String var) {
		/* TODO
		 * Iterate over all constraints and check if var is contained
		 * in the constraint's scope. If so, all _other_ variables of
		 * the constraint's scope are neighbors.
		 */
		Set<String> set=new HashSet<>();
		for(int i=0;i<csp.constraints.size();i++){
			int index=csp.constraints.get(i).getScope().indexOf(var);
			int size=csp.constraints.get(i).getScope().size();
			if(index!=-1){
				set.add(csp.constraints.get(size-1-index).toString());
			}
		}
		return set;
	}
}
