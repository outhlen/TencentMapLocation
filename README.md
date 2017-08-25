# 腾讯地图定位


## Gradle
app的build.gradle中添加
```
dependencies {
    compile 'com.github.outhlen:TencentMapLocation:latest.release'
}
```
工程的build.gradle中添加
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

## Usage
#### HttpDemo
Http的主要操作类为TencentLocationHelper，进行定位回调，以下为此功能说明
```
//首先建一个返回对象的基类，此类实现ZReply接口，实现接口的三个函数，如下

  locationHelper  =  new TencentLocationHelper(this);
        locationHelper.startLocation(new TencentLocationHelper.OnGetLocation() {
            @Override
            public void getLocation(TencentLocation location) {
                Toast.makeText(MainActivity.this,"getAddress："+location.getAddress(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void locationFail() {
                Toast.makeText(MainActivity.this,"定位失败：",Toast.LENGTH_SHORT).show();
               ZConfirm dlg = new ZConfirm(mActivity);
                dlg.setTitle("定位失败, 是否尝试再次定位？")
                       .addSubmitListener(new ZDialog.ZDialogSubmitInterface() {

                            @Override
                            public boolean submit() {
                                tencentMapLocation();
                                return true;
                            }
                        });
                dlg.addCancelListener(new ZDialog.ZDialogCancelInterface() {
                    @Override
                    public boolean cancel() {
                        return true;
                    }
                });
                dlg.show();
            }
        });