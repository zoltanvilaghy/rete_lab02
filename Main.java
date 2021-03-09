package hu.bme.mit.yakindu.analysis.workhere;

import java.util.List;
import java.util.Scanner;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Direction;
import org.yakindu.base.types.Event;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Entry;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		Scope defaultScope = s.getScopes().get(0);
		
		System.out.println("Bemenő események:");
		for(int i = 0; i < defaultScope.getEvents().size(); ++i) {
			Event event = defaultScope.getEvents().get(i);
			if(event.getDirection()==Direction.IN)
				System.out.println(event.getName());
		}
		System.out.println("Belső változók:");
		for(int i = 0; i < defaultScope.getVariables().size(); ++i) {
			Property variable = defaultScope.getVariables().get(i);
			System.out.println(variable.getName());			
		}
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				if(state.getOutgoingTransitions().size()==0)
					System.out.println("WARNING NO OUTGOING TRANSITIONS:");
				if(state.getName()!=null)
					System.out.println(state.getName());
				else
					System.out.println("Seems like this state doesn't have a name, how about something nice, like kiskutyus?");
			}
			if(content instanceof Transition) {
				Transition transition = (Transition) content;
				System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
			}			
		}
		
		ExampleStatemachine es = new ExampleStatemachine();
		print(es);
		
		konzolrolOlvasoProgram(es, defaultScope.getEvents(), defaultScope.getVariables());
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static void print(IExampleStatemachine s) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		EObject root = manager.loadModel("model_input/example.sct");
		Statechart es = (Statechart) root;
		Scope defaultScope = es.getScopes().get(0);
		List <Property> variables = defaultScope.getVariables();
		for(int i=0; i<variables.size(); ++i) {
			String name = variables.get(i).getName();
			name = name.substring(0,1).toUpperCase() + name.substring(1);
			System.out.println("public static void get" + name);
		}
	}
	
	public static void konzolrolOlvasoProgram(IExampleStatemachine s, List <Event> events, List <Property> variables) {
		System.out.println("ExampleStatemachine s = new ExampleStatemachine();");
		System.out.println("s.setTimer(new TimerService());");
		System.out.println("RuntimeService.getInstance().registerStatemachine(s, 200);");
		System.out.println("s.init();");
		System.out.println("s.enter();");
		System.out.println("s.runCycle();");
		System.out.println("print(s);");
		System.out.println("boolean exit = false;");
		System.out.println("Scanner terminalInput = new Scanner(System.in);");
		System.out.println("while(!exit) {");
		System.out.println("String command = terminalInput.nextLine();");
		for(int i=0; i<events.size(); ++i) {
			String name = events.get(i).getName();
			name = name.substring(0,1).toUpperCase() + name.substring(1);
			System.out.println("if(command.equals(\"" + events.get(i).getName() + "\"))");
			System.out.println("s.raise" + name + "();");
		}
		System.out.println("if(command.equals(\"exit\"))");
		System.out.println("exit=true;");
		System.out.println("s.runCycle();");
		System.out.println("print(s);");
		System.out.println("terminalInput.close();");
		System.out.println("System.exit(0);");
	}
}
