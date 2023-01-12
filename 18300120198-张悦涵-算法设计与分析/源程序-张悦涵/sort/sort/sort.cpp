// sort.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//

#include <iostream>
#include<vector>
#include <math.h>
#include<algorithm>
#include<chrono>
using namespace std;
using namespace chrono;

//随机函数，随机产生10^n次方的整数数据用于排序
int* randData(int n)
{
    int num = pow(10, n);
    int* res = new int[num];
    for (int i = 0; i < num; i++)
    {
        res[i] = rand();
    }
    return res;
}

//选择排序，时间复杂度为O(n^2)，空间复杂度为O(n)
void select_sort(int *nums, int l, int r)
{
    int tmp, index;
    for (int i = l; i < r; i++)
    {
        index = i;
        for (int j = i + 1; j <= r; j++)
        {
            if (nums[j] < nums[index])
            {
                index = j;
            }
        }
        tmp = nums[i];
        nums[i] = nums[index];
        nums[index] = tmp;
    }
}

//插入排序，时间复杂度为O(n^2)，空间复杂度为O(1)
void insert_sort(int *nums, int l, int r)
{
    for (int j = l; j <= r; j++)
    {
        int k = nums[j];
        int i = j - 1;
        while (i >= 0 && k < nums[i])
        {
            nums[i + 1] = nums[i];
            i--;
        }
        nums[i + 1] = k;
    }
}

//合并排序，时间复杂度为O(nlogn)，空间复杂度为O(n)
void merge(int* nums, int low, int mid, int high)
{
    int i = low, j = mid + 1, k = 0;  
    int* temp = new int[high - low + 1]; 
    while (i <= mid && j <= high) {
        if (nums[i] <= nums[j])
            temp[k++] = nums[i++];
        else
            temp[k++] = nums[j++];
    }
    while (i <= mid)
        temp[k++] = nums[i++];
    while (j <= high)
        temp[k++] = nums[j++];
    for (i = low, k = 0; i <= high; i++, k++)
        nums[i] = temp[k];
    delete[]temp;
}

void merge_sort(int* nums, int low, int high)
{
    if (low >= high) { return; } 
    int mid = low + (high - low) / 2;  
    merge_sort(nums, low, mid);
    merge_sort(nums, mid + 1, high);
    merge(nums, low, mid, high);
}

//快速排序,时间复杂度为O(nlogn)，空间复杂度为O(n)
void swap(int* nums, int i, int j)
{
    int tmp = nums[i];
    nums[i] = nums[j];
    nums[j] = tmp;
}
int median(int* nums, int l, int r)
{
    int m = (l + r) / 2;
    if (nums[m] < nums[l])
    {
        swap(nums, m, l);
    }
    if (nums[r] < nums[l])
    {
        swap(nums, r, l);
    }
    if (nums[r] < nums[m])
    {
        swap(nums, r, m);
    }
    swap(nums, m, r - 1);
    return nums[r - 1];
}

void quick_sort(int* nums, int l, int r)
{
    if (l + 10 <= r)
    {
        int p = median(nums, l, r);
        int i = l, j = r - 1;
        for (;;)
        {
            while (nums[++i] < p) {};
            while (p < nums[--j]) {};
            if (i < j)
            {
                swap(nums, i, j);
            }
            else
                break;
        }
        swap(nums, i, r - 1);
        quick_sort(nums, l, i - 1);
        quick_sort(nums, i + 1, r);
    }
    else
    {
        insert_sort(nums, l, r);
    }
}
//希尔排序，最坏时间复杂度为O(n^2)，空间复杂度为O(1)
void shell_sort(int* nums, int n)
{
    for (int gap = n / 2; gap > 0; gap /= 2)
    {
        for (int i = gap; i < n; i++)
        {
            int tmp = nums[i];
            int j = i;
            for (; j >= gap && tmp < nums[j - gap]; j -= gap)
            {
                nums[j] = nums[j - gap];
            }
            nums[j] = tmp;
        }
    }
}

//随机化快速排序，时间复杂度为O(nlogn)，空间复杂度为O(logn)
int partition(int* nums, int l, int r)
{
    int p = rand() % (r - l + 1) + l;
    swap(nums, p, r);
    return nums[r];
}

void quick_random_sort(int *nums, int l, int r)
{
    if (l + 10 <= r)
    {
        int p = partition(nums, l, r);
        int i = l - 1, j = r;
        for (;;)
        {
            while (nums[++i] < p) {};
            while (p < nums[--j]) {};
            if (i < j)
            {
                swap(nums, i, j);
            }
            else
                break;
        }
        swap(nums, i, r - 1);
        quick_sort(nums, l, i - 1);
        quick_sort(nums, i + 1, r);
    }
    else
    {
        insert_sort(nums, l, r);
    }
}
//测试不同排序函数在10^2、10^3、10^4、10^5、10^6的数据集上的使用时间
void test(string sort_type, int* data, int n)
{
    if (sort_type == "select_sort")
    {
        auto t0 = system_clock::now();
        select_sort(data, 0, n - 1);
        auto t1 = system_clock::now();
        auto d = duration_cast<nanoseconds>(t1 - t0);
        cout << "选择排序在数据集大小为 "<<n<<" 所用的测试时间: "<< (double)d.count() / 1000 << "微秒" << endl;
    }
    else if (sort_type == "insert_sort")
    {
        auto t0 = system_clock::now();
        insert_sort(data, 0, n - 1);
        auto t1 = system_clock::now();
        auto d = duration_cast<nanoseconds>(t1 - t0);
        cout << "插入排序在数据集大小为 " << n << " 所用的测试时间: " << (double)d.count() / 1000 << "微秒" << endl;
    }
    else if (sort_type == "merge_sort")
    {
        auto t0 = system_clock::now();
        merge_sort(data, 0, n - 1);
        auto t1 = system_clock::now();
        auto d = duration_cast<nanoseconds>(t1 - t0);
        cout << "合并排序在数据集大小为 " << n << " 所用的测试时间: " << (double)d.count() / 1000 << "微秒" << endl;
    }
    else if (sort_type == "quick_sort")
    {
        auto t0 = system_clock::now();
        quick_sort(data, 0, n - 1);
        auto t1 = system_clock::now();
        auto d = duration_cast<nanoseconds>(t1 - t0);
        cout << "快速排序在数据集大小为 " << n << " 所用的测试时间: " << (double)d.count() / 1000 << "微秒" << endl;
    }
    else if (sort_type == "shell_sort")
    {
        auto t0 = system_clock::now();
        shell_sort(data, n);
        auto t1 = system_clock::now();
        auto d = duration_cast<nanoseconds>(t1 - t0);
        cout << "希尔排序在数据集大小为 " << n << " 所用的测试时间: " << (double)d.count() / 1000 << "微秒" << endl;
    }
    else
    {
        auto t0 = system_clock::now();
        quick_random_sort(data, 0, n-1);
        auto t1 = system_clock::now();
        auto d = duration_cast<nanoseconds>(t1 - t0);
        cout << "随机化快速排序在数据集大小为 " << n << " 所用的测试时间: " << (double)d.count() / 1000 << "微秒" << endl;
    }
}

int main() 
{
    srand((unsigned)time(NULL));
    int* data2 = randData(2);
    int* data3 = randData(3);
    int* data4 = randData(4);
    int* data5 = randData(5);
    int* data6 = randData(6);

    int* select_data2 = new int[100];
    int* select_data3 = new int[1000];
    int* select_data4 = new int[10000];
    int* select_data5 = new int[100000];
    int* select_data6 = new int[1000000];

    //插入排序
    cout << "插入排序:" << endl;
    memcpy(select_data2, data2, sizeof(data2));
    memcpy(select_data3, data3, sizeof(data3));
    memcpy(select_data4, data4, sizeof(data4));
    memcpy(select_data5, data5, sizeof(data5));
    memcpy(select_data6, data6, sizeof(data6));
    test("insert_sort", select_data2, 100);
    test("insert_sort", select_data3, 1000);
    test("insert_sort", select_data4, 10000);
    test("insert_sort", select_data5, 100000);
    test("insert_sort", select_data6, 1000000);
    
    //选择排序
    cout << "选择排序:" << endl;
    memcpy(select_data2, data2, sizeof(data2));
    memcpy(select_data3, data3, sizeof(data3));
    memcpy(select_data4, data4, sizeof(data4));
    memcpy(select_data5, data5, sizeof(data5));
    memcpy(select_data6, data6, sizeof(data6));
    test("select_sort", select_data2, 100);
    test("select_sort", select_data3, 1000);
    test("select_sort", select_data4, 10000);
    test("select_sort", select_data5, 100000);
    test("select_sort", select_data6, 1000000);

    //合并排序
    cout << "合并排序:" << endl;
    memcpy(select_data2, data2, sizeof(data2));
    memcpy(select_data3, data3, sizeof(data3));
    memcpy(select_data4, data4, sizeof(data4));
    memcpy(select_data5, data5, sizeof(data5));
    memcpy(select_data6, data6, sizeof(data6));
    test("merge_sort", select_data2, 100);
    test("merge_sort", select_data3, 1000);
    test("merge_sort", select_data4, 10000);
    test("merge_sort", select_data5, 100000);
    test("merge_sort", select_data6, 1000000);

    //快速排序
    cout << "快速排序:" << endl;
    memcpy(select_data2, data2, sizeof(data2));
    memcpy(select_data3, data3, sizeof(data3));
    memcpy(select_data4, data4, sizeof(data4));
    memcpy(select_data5, data5, sizeof(data5));
    memcpy(select_data6, data6, sizeof(data6));
    test("quick_sort", select_data2, 100);
    test("quick_sort", select_data3, 1000);
    test("quick_sort", select_data4, 10000);
    test("quick_sort", select_data5, 100000);
    test("quick_sort", select_data6, 1000000);

    //希尔排序
    cout << "希尔排序:" << endl;
    memcpy(select_data2, data2, sizeof(data2));
    memcpy(select_data3, data3, sizeof(data3));
    memcpy(select_data4, data4, sizeof(data4));
    memcpy(select_data5, data5, sizeof(data5));
    memcpy(select_data6, data6, sizeof(data6));
    test("shell_sort", select_data2, 100);
    test("shell_sort", select_data3, 1000);
    test("shell_sort", select_data4, 10000);
    test("shell_sort", select_data5, 100000);
    test("shell_sort", select_data6, 1000000);
    
    //随机快速排序
    cout << "随机快速排序:" << endl;
    memcpy(select_data2, data2, sizeof(data2));
    memcpy(select_data3, data3, sizeof(data3));
    memcpy(select_data4, data4, sizeof(data4));
    memcpy(select_data5, data5, sizeof(data5));
    memcpy(select_data6, data6, sizeof(data6));
    test("random_quick_sort", select_data2, 100);
    test("random_quick_sort", select_data3, 1000);
    test("random_quick_sort", select_data4, 10000);
    test("random_quick_sort", select_data5, 100000);
    test("random_quick_sort", select_data6, 1000000);

    delete []data2;
    delete []data3;
    delete []data4;
    delete []data5;
    delete []data6;
    return 0;
}


