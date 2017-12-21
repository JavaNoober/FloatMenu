# FloatMenu
A menu style pop-up window that mimics WeChat。仿微信的长按菜单。

##效果如下

![](https://github.com/JavaNoober/FloatMenu/tree/master/floatmenu.gif)

##引入方法:

    dependencies {
         ....
    
         compile 'com.noober.floatmenu:common:1.0.2'
    }
    
##使用说明
使用方法1:
    Activity 重写dispatchTouchEvent，并且新建一个Point对象，show的时候传入改对象
    
    	private Point point = new Point();
    	@Override
    	public boolean dispatchTouchEvent(MotionEvent ev) {
    		if(ev.getAction() == MotionEvent.ACTION_DOWN){
    			point.x = (int) ev.getRawX();
    			point.y = (int) ev.getRawY();
    		}
    		return super.dispatchTouchEvent(ev);
    	}
    
   调用：
   
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FloatMenu floatMenu = new FloatMenu(MainActivity.this);
                floatMenu.items("菜单1", "菜单2", "菜单3");
                floatMenu.show(point);
            }
        });

使用方法2:
    不需要重写dispatchTouchEvent，但是需要在初始化的时候传入所点击的view，**floatment初始化必须在点击事件外部**
    
    		final FloatMenu floatMenu = new FloatMenu(this, btn1);
    		floatMenu.items("菜单1", "菜单2", "菜单3");
    		floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
    			@Override
    			public void onClick(View v, int position) {
    				Toast.makeText(MainActivity.this, "菜单"+position, Toast.LENGTH_SHORT).show();
    			}
    		});
    		
设置菜单的方式:
    1.代码设置
    
            floatMenu.items("菜单1", "菜单2", "菜单3");
            ...
            List<String> list = new ArrayList<>();
            list.add("菜单1"); list.add("菜单2"); list.add("菜单3");
            floatMenu.items(list);
            
   2.menu.xml设置
            
            <menu xmlns:android="http://schemas.android.com/apk/res/android"
                  xmlns:app="http://schemas.android.com/apk/res-auto">
                <item app:menu_title="Send to Chat" app:icon = "@drawable/brush"></item>
                <item
                    app:menu_title="Add to Favorites" app:icon = "@drawable/barrage"/>
                <item
                    app:menu_title="Delete" app:icon = "@drawable/clock"/>
                <item
                    app:menu_title="More" app:icon = "@drawable/collection"/>
            
            </menu>
            
            floatMenu.inflate(R.menu.popup_menu);
            
   方法都提供参数设置菜单的宽度:
   
             public void items(List<MenuItem> itemList, int itemWidth)
             
             public void items(int itemWidth, String... items) 
             
             public void inflate(int menuRes, int itemWidth)
###创建菜单：
	
####注意点:
#####显示floatmenu有两种方法：
floatmenu.show(point)与floatmenu.show()的区别
区别在于，**创建floatmenu对象的位置**，**如果使用第2种方法，创建floatmenu必须在点击事件的外面**，
第一种则没有这种限制，如下所示:
    
    final FloatMenu floatMenu = new FloatMenu(this, btn2);
    floatMenu.inflate(R.menu.popup_menu);
    floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            Toast.makeText(MainActivity.this, "菜单"+position, Toast.LENGTH_SHORT).show();
        }
    });
    
	listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FloatMenu floatMenu = new FloatMenu(MainActivity.this);
				floatMenu.items("菜单1", "菜单2", "菜单3");
				floatMenu.show(point);
			}
		});
#####new FloatMenu(this, view) 与 new FloatMenu(this)区别
使用第二种方法创建对象在，必须重写dispatchTouchEvent
	