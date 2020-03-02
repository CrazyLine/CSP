import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BacktrackingSudokuLCVSolver implements CSPSolver {
    private int iterationCount;

    /**
     * Initiates the backtracking search for this CSP.
     * @return A consistent assignment for this CSP
     */
    @Override
    public <E> CSPResult<E> solve(CSP<E> csp) {
        iterationCount = 0;
        Assignment<E> assignment=new Assignment<>();
        for (String i:csp.domains.keySet()){
            if (csp.domains.get(i).size()==1){
                assignment.put(i,csp.domains.get(i).get(0));
            }
        }
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

    /**
     * This method returns the values of the domain of a variable in
     * a specific order. Can be used to implement e.g. least-constraining-value heuristic.
     * For this algorithm, it can be in any order, e.g. the arbitrary order in which they are
     * stored in the csp.
     * @param variable The variable for which the domain values should be returned
     * @param assignment The current assignment
     * @return An ordering of all domain values of the given variable.
     */
    protected <E> List<E> orderDomainValues(CSP<E> csp, String variable, Assignment<E> assignment) {
        return csp.domains.get(variable);
    }

    /**
     * This method returns some inference, which is basically a set of domain values that can be safely
     * deleted from the domains of the csp. To be used to implement e.g. forward-checking heuristic.
     * If it returns null this means there is some failure and we should back-track.
     * For this algorithm, it can always return an empty inference.
     * @param assignment The current assignment
     * @param var The selected variable
     * @param value The value for the given variable
     * @return An inference based on the current state of the csp, or null if there is a failure.
     */
    protected <E> Inference<E> inference(CSP<E> csp, Assignment<E> assignment, String var, E value) {
        // return an empty inference. We cannot return null, because this has a different meaning.
        Set<E> set=new HashSet<>();
        Inference<E> inference=new Inference<>();
        for (int i=0;i<csp.constraints.size();i++){
            List<String> list=csp.constraints.get(i).getScope();
            if (list.get(0)==var){
                for (int j=0;j<csp.domains.get(list.get(1)).size();j++){
                    if (value==csp.domains.get(list.get(1)).get(j)){
                        set.add(value);
                        inference.put(list.get(1),set);
                    }
                }
            }
        }
        return inference;
        //return new Inference<E>();
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

        ArrayList<Integer> map=new ArrayList<>();
        ArrayList<Integer> map1=new ArrayList<>();
        Assignment<E> copy=new Assignment<>();
        copy.putAll(assignment);
        //LCV.........................
        for(int i=0;i<orderDomainValues(csp,var,copy).size();i++){
            copy.put(var,orderDomainValues(csp,var,copy).get(i));
//            if(csp.isConsistent(copy)) {
            int numlcv = 0;
            boolean flag=true;
            for (int j = 0; j < csp.constraints.size(); j++) {
                List<String> list = csp.constraints.get(j).getScope();
                int inde=list.indexOf(var);
                if (inde!=-1) {
                    List<E> values = orderDomainValues(csp, list.get(list.size()-1-inde), copy);
                    List<E> copyvalues = new ArrayList<>();
                    for (int co = 0; co < values.size(); co++) {
                        copyvalues.add(values.get(co));
                    }
                    for (int k = 0; k < csp.constraints.size(); k++) {
                        List<String> neighbors = csp.constraints.get(k).getScope();
                        int index=neighbors.indexOf(list.get(list.size()-1-inde));
                        if (index!=-1) {
                            if (copy.get(neighbors.get(neighbors.size()-1-index)) != null) {
                                int index1 = copyvalues.indexOf(copy.get(neighbors.get(neighbors.size()-1-index)));
                                //System.out.println("assignment "+neighbors.get(0)+" "+copy.get(neighbors.get(0))+" "+list.get(1));
                                if (index1 != -1) {
                                    copyvalues.remove(index1);
                                }
                                if (copyvalues.size()==0){
                                    flag=false;
                                }
                            }
                        }
                    }
                    numlcv += copyvalues.size();
                }
            }
            if (flag){
                map.add(numlcv);
                map1.add(i);
            }
            copy.remove(var);
//            }else {
//                copy.remove(var);
//            }
        }

//        for (int j=0;j<map.size();j++){
//            System.out.println("itrate: "+iterationCount+" current: "+var+" "+map.get(j)+" "+map1.get(j));
//        }

        for(int i=0;i<map1.size();i++){
            for (int j = 0; j < map.size(); ++j) {
                boolean flag = false;
                for (int k = 0; k < map.size() - j - 1; ++k) {
                    if (map.get(k) < map.get(k + 1)) {
                        int temp =map.get(k);
                        map.set(k,map.get(k+1));
                        map.set(k+1,temp);
                        int temp1=map1.get(k);
                        map1.set(k,map1.get(k+1));
                        map1.set(k+1,temp1);
                        flag = true;
                    }
                }
                if (!flag) break;
            }
            if (!map1.isEmpty()){
                int vp=map1.get(i);
                //System.out.println(iterationCount+" "+var+" "+vp);
                assignment.put(var,orderDomainValues(csp,var,assignment).get(vp));
                if(csp.isConsistent(assignment)){
                    Inference<E> inference=inference(csp,assignment,var,orderDomainValues(csp,var,assignment).get(vp));
                    //System.out.println("currentpos: "+var+" inference: "+inference);
                    if (inference!=null) {
                        inference.reduceDomain(csp);
//					for(String key:csp.domains.keySet()){
//						System.out.println(csp.domains.get(key));
//					}
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
