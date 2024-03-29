/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.mr.analysis;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import smrl.mr.language.ExpressionPassTag;
import smrl.mr.language.MR;
import smrl.mr.language.MRDataProvider;

public class ASMUtil {

	public static void main(String args[]) throws IOException{
		
	}

	private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);
    
	public static String insnToString(AbstractInsnNode insn){
//		System.out.println(insn.getClass().getCanonicalName());
		insn.accept(mp);
		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString();
	}

	public static List<String> retrieveDataConsideredInMR(ASM_MRData _mrData) throws IOException {
		
		ArrayList<String> data = _mrData.getData(); 
		
		//remove duplicates and keep sorted
		TreeSet<String> t = new TreeSet<>();
		t.addAll(data);
		
		ArrayList<String> res = new ArrayList<>();
		res.addAll(t);
		
		return res;
//		data.add(e);
	}

	public static ASM_MRData extractMRData(MR mr) throws IOException {
		ASM_MRData _mrData = extractMRData(mr.getClass());
		return _mrData;
	}
	
	public static class ASM_MRData {
		
		private int expressionPassCounter;
		private ArrayList<String> data;

		public ASM_MRData(int expressionPassCounter, ArrayList<String> data) {
			this.expressionPassCounter = expressionPassCounter;
			this.data = data;
		}

		public void add(ASM_MRData _MRData) {
			expressionPassCounter += _MRData.expressionPassCounter;
			data.addAll(_MRData.data);
		}

		public int getExpressionPassCounter() {
			return expressionPassCounter;
		}

		public ArrayList<String> getData() {
			return data;
		}
		
		
	}

	private static ASM_MRData extractMRData(Class mr) throws IOException {
		
		int expressionPassCounter = 0;
		
		ArrayList<String> data = new ArrayList<>();
		ClassReader reader = new ClassReader(mr.getCanonicalName());
		ClassNode classNode = new ClassNode();
		reader.accept(classNode,0);
		@SuppressWarnings("unchecked")
		final List<MethodNode> methods = classNode.methods;
		for(MethodNode m: methods){
			InsnList inList = m.instructions;
//			System.out.println(m.name);
			
			if ( ! m.name.equals("mr") ){
				continue;
			}
			
			for(int i = 0; i< inList.size(); i++){
				AbstractInsnNode inst = inList.get(i);
				if ( inst.getOpcode() == Opcodes.INVOKEVIRTUAL || inst.getOpcode() == Opcodes.INVOKESTATIC){
					if( inst instanceof org.objectweb.asm.tree.MethodInsnNode ){
						MethodInsnNode _inst = (MethodInsnNode) inst;
						String meth = _inst.name;
						String clazz = _inst.owner;
			
						if ( isAnnotatedAsMRDataProvider( clazz, meth ) ){
							data.add(meth);
						}
						
						if ( isAnnotatedWithExpressionPassTag( clazz, meth ) ) {
							expressionPassCounter++;
						}
						
//						System.out.println(clazz+"."+meth);
					}
					
				}
//				System.out.print(insnToString(inList.get(i)));
			}
		}
		
		ASM_MRData res = new ASM_MRData( expressionPassCounter, data );

		Class<?> superCLass = mr.getSuperclass();
		if( superCLass != null && ( ! superCLass.equals(Object.class) )){
			res.add(extractMRData(superCLass));
		}
		
		
		return res;
	}
	
	private static boolean isAnnotatedWithExpressionPassTag(String clazz, String meth) {
//		System.out.println("!!!"+clazz+" "+meth);
//		System.out.println("!!!");
		try {
			for ( Method m : Class.forName(clazz.replace('/', '.')).getMethods() ){
				if ( m.getName().equals(meth) ){
					if ( m.getAnnotations() != null ){
						for ( Annotation ann : m.getAnnotations() ){
							if ( ann.annotationType().getCanonicalName().equals(ExpressionPassTag.class.getCanonicalName()) ){
								return true;
							}
						}
					}
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static boolean isAnnotatedAsMRDataProvider(String clazz, String meth) {
//		System.out.println("!!!"+clazz+" "+meth);
//		System.out.println("!!!");
		try {
			for ( Method m : Class.forName(clazz.replace('/', '.')).getMethods() ){
				if ( m.getName().equals(meth) ){
					if ( m.getAnnotations() != null ){
						for ( Annotation ann : m.getAnnotations() ){
							if ( ann.annotationType().getCanonicalName().equals(MRDataProvider.class.getCanonicalName()) ){
								return true;
							}
						}
					}
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
