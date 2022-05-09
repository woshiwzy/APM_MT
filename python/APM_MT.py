import datetime
import os
import os.path
import webbrowser

from pyecharts import options as opts
from pyecharts.charts import Bar, Page, Line
from pyecharts.commons.utils import JsCode


##=================================================
## Action是日志封装文件

class Action:
    """
    Action 抽象類
    """

    def __init__(self, done, method, start, start_java_heap, start_native_heap, start_power, cost=0, finish_java_heap=0,
                 finish_native_heap=0, end_power=0):
        self.done = done
        self.method = method
        self.start = start
        self.start_java_heap = start_java_heap
        self.start_native_heap = start_native_heap
        self.start_power = start_power

        self.cost = cost
        self.finish_java_heap = finish_java_heap
        self.finish_native_heap = finish_native_heap
        self.end_power = end_power

    def __str__(self):
        desc = "" + str(
            self.done), self.method, self.start, self.start_java_heap, self.start_native_heap, self.start_power, self.cost, self.finish_java_heap, self.finish_native_heap, self.end_power
        return desc

    def data_done(self):
        return self.done

    @staticmethod
    def create(done, method, start, start_java_heap, start_native_heap, start_power, cost=0, finish_java_heap=0,
               finish_native_heap=0, end_power=0):
        '''
        :param end_power:
        :param start_power:
        :param method:
        :param start:
        :param start_java_heap:
        :param start_native_heap:
        :param cost:
        :param finish_java_heap:
        :param finish_native_heap:
        :return:
        '''

        action = Action(done, method, start, start_java_heap, start_native_heap, start_power, cost, finish_java_heap,
                        finish_native_heap, end_power)
        return action


##=================================================
def read_log_2_actions(dirs):
    """
    读取指定目录下的logfile转换成python 对象,
    """
    log_list = []
    files = []  # 日志文件
    try:
        files = os.listdir(dirs)
    except Exception as e:
        print('=====>>没找到文件，请确认设备是否连接')
        return None

    for logfile in files:
        if logfile.startswith("mt_") and logfile.endswith(".txt"):
            # print("-------------------------------------------------")
            abs_path = dirs + os.path.sep + logfile
            # print("log file abspath", abs_path)
            opf = open(abs_path, 'r', encoding='utf-8')
            content = opf.readlines()
            for line in content:
                line = line.strip()
                if line is None:
                    continue
                else:
                    # print(line)
                    # MT_D,com.sand.apm.mt.MainActivity$1.onClick,1651891254864,2.0MB,23.0MB,99,983,3.0MB,32.0MB,99
                    metas = line.split(",")
                    done = metas[0]
                    method = metas[1] + ":" + metas[2]
                    start = metas[3]

                    start_java_heap = metas[4].replace("MB", "")
                    start_native_heap = metas[5].replace("MB", "")
                    start_power = int(metas[6])

                    try:
                        cost = int(metas[7])
                        finish_java_heap = float(metas[8].replace("MB", ""))
                        finish_native_heap = float(metas[9].replace("MB", ""))
                        end_power = int(metas[10])
                    except Exception as e:
                        cost = 0
                        finish_java_heap = 0
                        finish_native_heap = 0
                        end_power = 0
                        pass

                    action = Action.create(True if done.__eq__("MT_D") else False,
                                           method,
                                           start,
                                           start_java_heap,
                                           start_native_heap,
                                           start_power,
                                           cost,
                                           finish_java_heap,
                                           finish_native_heap,
                                           end_power
                                           )
                    # print(action.__str__())
                    log_list.append(action)
    return log_list


def render_logs(log_file_path):
    """
    读取指定mt log 目录下的文件
    :param log_file_path:
    :return:
    """
    datas = read_log_2_actions(log_file_path)
    if datas is None:
        return None

    dic = {}
    mt_datas = []
    mt_datas_label = []

    mt_mem_start_java = []
    mt_mem_start_native = []

    mt_mem_end_java = []
    mt_mem_end_native = []

    mt_power_start = []
    mt_power_end = []

    mt_anr_data = []

    ##设置图标显示的颜色，耗时超过5000显示红色，100-5000显示橙色，其他显示绿色
    color_function = """
            function (params) {
                if (params.value > 5000) {
                    return 'red';
                } else if (params.value > 1000 && params.value < 5000) {
                    return 'orange';
                }
                return 'green';
            }
            """

    # 组装数据
    for action in datas:
        # time
        mt_datas.append(action.cost)
        mt_datas_label.append(action.method)

        # mem
        mt_mem_start_java.append(float(action.start_java_heap))
        mt_mem_start_native.append(float(action.start_native_heap))

        mt_mem_end_java.append(float(action.finish_java_heap))
        mt_mem_end_native.append(float(action.finish_native_heap))

        # power
        mt_power_start.append(action.start_power)
        mt_power_end.append(action.end_power)
        # anr
        if not action.done or action.cost >= 5000:
            cost = action.cost if action.cost > 0 else 9999
            mt_anr_data.append(cost)
        # mt_datas_label.append("")
        pass

    # 1.方法执行耗时======================================================================================================
    # page = Page(layout=Page.SimplePageLayout, page_title="APM数据展示")
    # ====方法执行耗时
    # bar = Bar(init_opts=opts.InitOpts(width="100%",
    #                             height="400px",
    #                             theme=ThemeType.DARK))

    page = Page(layout=Page.DraggablePageLayout, page_title="APM数据展示")

    method_cost_bar = Bar()
    method_cost_bar.add_xaxis(mt_datas_label)
    method_cost_bar.add_yaxis("方法耗时(main开头是主线程，其他是子线程)", mt_datas, label_opts=opts.LabelOpts(is_show=True),
                              itemstyle_opts=opts.ItemStyleOpts(color=JsCode(color_function)))
    # bar.reversal_axis() //旋转
    method_cost_bar.set_global_opts(
        xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=-80), is_show=False),
        title_opts=opts.TitleOpts(title="耗時(毫秒)", subtitle="方法运行耗时"),
        datazoom_opts=opts.DataZoomOpts(),
        # datazoom_opts=opts.DataZoomOpts(orient="vertical", range_start=0, range_end=20000),
        # datazoom_opts=opts.DataZoomOpts(orient="horizontal"),
        # datazoom_opts=opts.DataZoomOpts(type_="inside"),
    )

    method_cost_bar.set_series_opts(
        label_opts=opts.LabelOpts(is_show=False),
        markline_opts=opts.MarkLineOpts(
            data=[opts.MarkLineItem(y=5000, name="耗时超过5000毫秒")]
        ),
    )
    page.add(method_cost_bar)

    # 2.内存数据======================================================================================================
    mem_data_bar = (
        Bar().add_xaxis(mt_datas_label).
            add_yaxis("开始时Java內存", mt_mem_start_java, stack="start").
            add_yaxis("开始时Native內存", mt_mem_start_native, stack="start").

            add_yaxis("结束时Java內存", mt_mem_end_java, stack="end").
            add_yaxis("结束时Native內存", mt_mem_end_native, stack="end").
            set_global_opts(

            # datazoom_opts=opts.DataZoomOpts(orient="vertical", range_start=0, range_end=256),
            datazoom_opts=opts.DataZoomOpts(),
            xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=-15), is_show=False),
            title_opts=opts.TitleOpts(title="内存(MB)", subtitle="方法运行前后内存"),
            # legend_opts=opts.LegendOpts(pos_top="48%"),
        )
        # .render("bar_rotate_xaxis_label.html")
    )
    mem_data_bar.set_series_opts(
        label_opts=opts.LabelOpts(is_show=False),
        datazoom_opts=opts.DataZoomOpts(orient="vertical"),
    )

    page.add(mem_data_bar)

    # 3.ANR======================================================================================================

    anr_bar = (
        Bar().add_xaxis(mt_datas_label).
            add_yaxis("ANR 函数", mt_anr_data).
            set_global_opts(
            xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=-15), is_show=False),
            title_opts=opts.TitleOpts(title="可能出现了ANR的函数(毫秒)",
                                      subtitle="9999代表没有收集到该函数执行结束信息(如果是主线程大概率出现ANR)，其他数值代表函数执行具体时间"),
            # datazoom_opts=opts.DataZoomOpts(orient="vertical", range_start=0,rn),
            datazoom_opts=opts.DataZoomOpts(range_start=0),
        )
        # .render("bar_rotate_xaxis_label.html")
    )
    anr_bar.set_series_opts(
        label_opts=opts.LabelOpts(is_show=False),
    )

    page.add(anr_bar)

    ## 4.电量曲线=======================================================================================================

    power_info_bar = (
        Bar().add_xaxis(mt_datas_label).
            add_yaxis("开始时电量", mt_power_start).
            add_yaxis("结束时电量", mt_power_end).
            set_global_opts(
            xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=-15), is_show=False),
            title_opts=opts.TitleOpts(title="电量(百分比)", subtitle="方法运行前后电量"),
            # datazoom_opts=opts.DataZoomOpts(orient="vertical", range_start=0, range_end=100),
            datazoom_opts=opts.DataZoomOpts(),
            # legend_opts=opts.LegendOpts(pos_top="48%"),
        )
        # .render("bar_rotate_xaxis_label.html")
    )
    power_info_bar.set_series_opts(
        label_opts=opts.LabelOpts(is_show=False),
    )

    page.add(power_info_bar)

    # ======================================================================================================
    # 生成临时目录，渲染文件存放目录
    time_label = datetime.datetime.now().strftime("%Y_%m_%d_%H_%M_%S")
    # html文件存放路径
    local_dir_name = "mtlogs_render"
    local_dir = os.getcwd() + os.path.sep + local_dir_name
    if not os.path.exists(local_dir):
        os.mkdir(local_dir)
    html_file_name = local_dir_name + os.path.sep + "apm_" + time_label + ".html"
    # 将渲染后的html放到准备好的目录下(mt_yyyy_MM_dd_HH:mm:ss)
    page.render(html_file_name)
    # html 文件 绝对路径
    abs_path = os.getcwd() + os.path.sep + html_file_name
    # 打印数据条数
    print("collect data size：", len(datas))

    return abs_path


def auto_create(package_name):
    """
    从shell脚本读取log文件
    :param package_name: 应用包名
    :return:
    """
    if package_name is None:
        print("请输入应用的包名")
        return None
    mt_files = "mt_" + datetime.datetime.now().strftime("%Y_%m_%d_%H_%M_%S")
    local_files = mt_files
    ret=os.system("adb pull /sdcard/Android/data/" + package_name + "/files/Download/mtfiles " + local_files)
    return mt_files


def auto_mt(package_name):
    """
    自动方式:只需要输入包名就可以自动导出日志生成渲染文件
    :return:
    """
    print("正在提取包名为:"+package_name+"的App日志文件")
    files = auto_create(package_name)
    if files is not None and os.path.exists(files):
        html_abs_path = render_logs(files)
        print("html文件保存到目录:", html_abs_path)
        webbrowser.open(html_abs_path, new=0, autoraise=True)
    else:
        print("未找到日志文件,请确保手机里连接到电脑并产生了日志文件")


def manu_mt():
    """
    手动方式:手动导出日志，放在指定目录render_logs读取这个目录自动生成渲染图
    日志文件默认存放位置:/sdcard/Android/data/package_name/files/Download/mtfiles
    :return:
    """
    html_abs_path = render_logs("D:\\workspace\\workspace\\testSpace\\APM_MT\\mtfiles")
    print("html文件保存到目录:", html_abs_path)
    webbrowser.open(html_abs_path, new=0, autoraise=True)


if __name__ == '__main__':
    # print('\033[31m这是红色字体')。
    """
        请使用Python 3 和 pyechats v1，运行前请确保手机连上电脑并有数据
    """
    ret = os.system("python -c \"import pyecharts\"")
    if ret != 0:
        print("######################################################\n")
        print("运行前请安装pyechats\n")
        print("pyechat 文档:https://pyecharts.org/#/zh-cn/quickstart\n")
        print("安装方法:pip install pyecharts\n")
        print("######################################################\n")
    else:
        # 第一种方式:提供包名脚本将自动读取日志文件生成渲染图
        auto_mt("com.sand.apm.mt")
        # 第二种方式:手动方式把logo导入到电脑某个目录，render_logs 从这个目录中读取数据，生成渲染
        # manu_mt()
