import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AC3CSPSolver implements CSPSolver{
    private int iterationCount;

    /**
     * Initiates the backtracking search for this CSP.
     * @return A consistent assignment for this CSP
     */
    @Override
    public <E> CSPResult<E> solve(CSP<E> csp) {
        iterationCount = 0;
        Assignment<E> finalAssignment = backtrack(csp, new Assignment<E>());
        return new CSPResult<E>(finalAssignment, iterationCount);
    }

    public <E> CSPResult<E> solve(CSP<E> csp,Assignment<E> assignment) {
        iterationCount = 0;
        assignment.put("Alabama",csp.domains.get("Alabama").get(0));
        Assignment<E> finalAssignment = backtrack(csp, assignment);
        return new CSPResult<E>(finalAssignment, iterationCount);
    }

    /**
     * Selects an unassigned variable. For this algorithm, it can be just the first, or a
     * randomly chosen unassigned variable.
     * @param assignment The assignment for which to determine an unassigned variable
     * @return A variable that is not assigned yet.
     */
    protected <E> String selectUnassignedVariable(CSP<E> csp, Assignment<E> assignment) {
        /* TODO
         * Return any unassigned variable. For the generic backtracking search
         * you can e.g. return a randomly chosen unassigned variable, or just the
         * first one.
         */
//		List<String> list=new ArrayList<>();
//		for(String i:csp.variables) {
//			boolean flag = assignment.containsKey(i);
//			if(!flag){
//				list.add(i);
//			}
//		}
//		int r=(int)(Math.random()*list.size());
//		return list.get(r);

        for(String i:csp.variables) {
            boolean flag = assignment.containsKey(i);
            if(!flag){
                return i;
            }
        }

        return null;
    }

    protected <E> List<E> orderDomainValues(CSP<E> csp, String variable, Assignment<E> assignment) {
        return csp.domains.get(variable);
    }

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

    /**
     * Actual recursive implementation of the backtracking search. The
     * implementation can pretty much follow the pseudo-code in the text book.
     * Basically, the algorithm can be implemented using almost only calls to the other
     * methods of this and the other classes.
     * @param assignment The current assignment
     * @return The updated assignment, or null if there is no valid assignment
     */
    private <E> Assignment<E> backtrack(CSP<E> csp, Assignment<E> assignment) {
        ++iterationCount;
        if(assignment.isComplete(csp)){
            return assignment;
        }
        String var=selectUnassignedVariable(csp,assignment);
        Assignment<E> result=new Assignment<>();
        for(int i=0;i<orderDomainValues(csp,var,assignment).size();i++){
            assignment.put(var,orderDomainValues(csp,var,assignment).get(i));
            if(csp.isConsistent(assignment)){
                Inference<E> inference=inference(csp,assignment,var,orderDomainValues(csp,var,assignment).get(i));
                //System.out.println("currentpos: "+var+" inference: "+inference);
                if (inference!=null) {
                    inference.reduceDomain(csp);
//					for(String key:csp.domains.keySet()){
//						System.out.println(csp.domains.get(key));
//					}
                    try {
                        AC3.ac3(csp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result=backtrack(csp,assignment);
                    inference.restoreDomain(csp);
                    if(result!=null){
                        return result;
                    }
                }
                assignment.remove(var);
            }
            else {
                assignment.remove(var);
            }
        }
        /* TODO
         * The implementation can follow very much the pseudo code in the book:
         *
         * if assignment is complete then return assignment
         * var <- SELECT-UNASSIGNED-VARIABLE(csp)
         * for each value in ORDER-DOMAIN-VALUES(var, assignment, csp) do
         *   if value is consistent with assignment then
         *     add {var = value} to assignment
         *     inferences <- INFERENCE(csp, var, value)
         *     if inferences != failure then
         *       add inferences to csp
         *       result <- BACKTRACK(assignment, csp)
         *       remove inferences from csp
         *       if result != failure then
         *         return result
         *     remove {var = value} from assignment
         * return failure
         *
         * Please note that "failure" is represented as null in this implementation.
         * I implemented the inferences slightly different than the book suggests:
         * The method inference() additionally needs the current assignment as a
         * parameter, and the inference is instead applied to the csp, not to the
         * assignment. This is realized by the methods Inference.reduceDomain() and
         * Inference.restoreDomain(). I slightly modified the pseudo code in contrast
         * to the code in the book to comply with the modified interface of the
         * inference.
         */
        return null;
    }
}
