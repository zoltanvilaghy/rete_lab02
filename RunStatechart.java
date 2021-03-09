package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.Scanner;

import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;



public class RunStatechart {
	
	public static void main(String[] args) throws IOException {
		ExampleStatemachine s = new ExampleStatemachine();
		s.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s, 200);
		s.init();
		s.enter();
		s.runCycle();
		print(s);
		boolean exit = false;
		Scanner terminalInput = new Scanner(System.in);
		while(!exit) {
			String command = terminalInput.nextLine();
			if(command.equals("start"))
				s.raiseStart();
			if(command.equals("black"))
				s.raiseBlack();
			if(command.equals("white"))
				s.raiseWhite();
			if(command.equals("exit"))
				exit=true;
			s.runCycle();
			print(s);
		}
		terminalInput.close();
		System.exit(0);
	}

	public static void print(IExampleStatemachine s) {
		System.out.println("W = " + s.getSCInterface().getWhiteTime());
		System.out.println("B = " + s.getSCInterface().getBlackTime());
	}
}
