package com.suse.chuan;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

//界面设置
public class view extends JFrame{
	//声明组件
	private JPanel p;
	private JButton btn1,btn2;
	private JLabel label1,label2,label3;
	private JComboBox combobox;
	private  String filePath;
	private String charset = "gbk";
	public   view() {
		//调用父类的构造方法，并设置标题
		super("java文件编码转换器");
		//创建面板对象
		p = new JPanel();
		FlowLayout layout = new FlowLayout();// 布局
		//文件选择器
	    label1 = new JLabel("请选择文件夹：");// 标签
	    btn1= new JButton("打开");// 钮1
	    //编码选择器
	    label2 = new JLabel("选择转换的编码");
	    combobox = new JComboBox();
	    combobox.addItem("GBK");
	    combobox.addItem("UTF-8");
	    //状态展示
	    label3 = new JLabel("您还未选择文件夹");
	    btn2 = new JButton("开始转换");
	    this.add(p);
	    p.setLayout(layout);
	    p.add(label1);
        p.add(btn1);
        p.add(label2);
        p.add(combobox);
        p.add(label3);
        p.add(btn2);
        //创建监听对象
       ButtonListener button = new ButtonListener();
       btn1.addActionListener(button);
       btn2.addActionListener(button);
		
		//设置窗体大小
		this.setSize(400, 200);
		//设置窗体初始位置
		this.setLocation(800, 300);
		//设置窗体的默认关闭为退出程序
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置窗口可见
		this.setVisible(true);
		
	}

	public void view() {
		// TODO Auto-generated method stub
		
	}

	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//按钮1事件方法
			if(e.getSource().equals(btn1)) {
				 JFileChooser fileChooser = new JFileChooser();
				   fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				   int returnVal = fileChooser.showOpenDialog(fileChooser);
				   if(returnVal == JFileChooser.APPROVE_OPTION){       
				        filePath= fileChooser.getSelectedFile().getAbsolutePath();//这个就是你选择的文件夹的路径 
				        label3.setText("当前选择路径为："+filePath);
				       
				   }
			}
			//按钮2事件的方法
			else if(e.getSource().equals(btn2)) {
				//文件夹选择判断
				if(label3.getText().equals("您还未选择文件夹"))
					JOptionPane.showMessageDialog(null, "请先选择文件夹", "错误提示", JOptionPane.ERROR_MESSAGE);
				else if(label3.getText().equals("文件转换完成"))
					JOptionPane.showMessageDialog(null, "请重新选择文件夹", "错误提示", JOptionPane.ERROR_MESSAGE);
				
				else {
					 
			        List<String> filelist = new ArrayList<String>();
			        String charset = combobox.getSelectedItem().toString();
			        findFile(filePath,filelist);
			        System.out.println(filelist);
			        for(String fileName : filelist){
			            convert(fileName, fileName, charset);
			         }
			        label3.setText("文件转换完成");
			        
				}
			}
		}
	}
	//找寻该文件夹以及子文件夹下所有以.java结尾的文件，并获得文件路径
	public void findFile(String fileDir,List<String> filelist) {
		
		fileDir = fileDir.replaceAll("\\\\", "\\\\\\\\");//替换路径名中的\为\\
		
		File dir = new File(fileDir);
		File[] files = dir.listFiles();
		String regex = ".java";
		Pattern p =Pattern.compile(regex);//正则表达式规则
		
			
		for(int i=0;i<files.length;i++) {
			if(files[i].isDirectory())
				findFile(files[i].getAbsolutePath(),filelist);//是文件，就递归
			else {
				String strFileName=files[i].getAbsolutePath().toLowerCase();
				Matcher m = p.matcher(strFileName);
				if(m.find())
					filelist.add(strFileName);//将符合的文件路径添加进集合中
			}
		}

	}
	//重新编码并覆盖原来文件
	public void convert(String oldFile,String newFile,String newCharset)  {
		BufferedReader bin;
		FileOutputStream fos;
		StringBuffer content = new StringBuffer();
		
		try {
			//判断文件编码
			judgeCharset(oldFile);
			//读取文件到缓存中
			bin = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(oldFile),charset));
			String line = null;
			while((line=bin.readLine())!=null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));//加入换行符
				
			}
			System.out.println(content);
			bin.close();
			
			 fos = new FileOutputStream(newFile);//设置输出路径
			 Writer out = new OutputStreamWriter(fos,newCharset);//设置输出流位置及其编码格式
			 out.write(content.toString());
			 out.close();
			 fos.close();
		}catch (UnsupportedEncodingException e) {
	         e.printStackTrace();
	      } catch (FileNotFoundException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
		
	}
	//粗略判断文件编码格式主要为gbk和utf-8
	public void judgeCharset(String url) throws FileNotFoundException, UnsupportedEncodingException {
		File infile = new File(url);//url为读取文件的路径

		//判断当前文件的编码格式

		FileInputStream fis =new FileInputStream(infile); 
		byte[] b=new byte[3];  
		if(b[0]==-17&&b[1]==-69&&b[2]==-65) {
		charset = "UTF-8";
		}else {
		charset = "gbk";
		} 
		
	}
}
