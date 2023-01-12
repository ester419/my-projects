import matplotlib.pyplot as plt


x = ["10^2", "10^3", "10^4", "10^5", "10^6"]
y_select_sort = [9.4, 781.1, 77161.7, 7704150, 770707000]
y_insert_sort = [0.9, 7.4, 72.2, 723.7, 7147.6]
y_merge_sort  = [27.8, 273.2, 2676.1, 28163, 292429]
y_quick_sort  = [2.7, 40.3, 380, 5151.9, 60566.1]
y_shell_sort  = [1.8, 31.7, 335.7, 4202.5, 50490.3]
y_random_quick_sort = [3, 30.1, 368.9, 5043.4, 58816]

plt.rcParams["font.sans-serif"]=["SimHei"]
plt.rcParams["axes.unicode_minus"]=False
plt.rcParams['savefig.dpi'] = 1000 #图片像素
plt.rcParams['figure.dpi'] = 1000 #分辨率

plt.title("不同排序算法在不同数据规模的时间复杂度")
plt.xlabel("数据规模")
plt.ylabel("排序用时/微秒")
#l1, = plt.plot(x, y_select_sort)
#l2, = plt.plot(x, y_insert_sort)
#l3, = plt.plot(x, y_merge_sort)
#l4, = plt.plot(x, y_quick_sort)
#l5, = plt.plot(x, y_shell_sort)
l6, = plt.plot(x, y_random_quick_sort)
#plt.legend(handles=[l1, l2, l3, l4, l5, l6],labels=['选择排序', '插入排序', '合并排序', '快速排序', '希尔排序', '随机快速排序'],loc='best')
#plt.legend(handles=[l2, l3, l4, l5, l6],labels=['插入排序', '合并排序', '快速排序', '希尔排序', '随机快速排序'],loc='best')
plt.legend(handles=[l6],labels=['随机快速排序'],loc='best')
plt.savefig("随机快速排序.png")
plt.show()