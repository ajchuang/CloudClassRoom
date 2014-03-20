using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

// @lfred added
using Microsoft.Office.Interop.PowerPoint;
using System.Windows.Forms;

namespace WpfApplication1
{

    

    /// <summary>
    /// MainWindow.xaml 的互動邏輯
    /// </summary>
    public partial class MainWindow : Window
    {
        string m_openPpt;
        Microsoft.Office.Interop.PowerPoint.Application     m_oPPT;
        Microsoft.Office.Interop.PowerPoint.Presentations   m_objPresSet;
        Microsoft.Office.Interop.PowerPoint.Presentation    m_objPres;

        public MainWindow()
        {
            InitializeComponent();
            m_openPpt = null;
        }

        private void exportCurrentSlide ()
        {
            int idx = m_objPres.SlideShowWindow.View.Slide.SlideIndex;
            m_objPres.SlideShowWindow.View.Slide.Export ("d:\\slides.png", "png", 1024, 768);            
        }
        
        private void prevBtn_Click(object sender, RoutedEventArgs e) {
            if (m_openPpt == null)
                return;

            m_objPres.SlideShowWindow.View.Previous();
            exportCurrentSlide();
        }

        private void nextBtn_Click(object sender, RoutedEventArgs e) {
            if (m_openPpt == null)
                return;

            m_objPres.SlideShowWindow.View.Next ();
            exportCurrentSlide();            
        }

        private void openBtn_Click(object sender, RoutedEventArgs e) {

            OpenFileDialog dialog = new OpenFileDialog ();

            if (dialog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                m_openPpt = dialog.FileName;

                //Create an instance of PowerPoint.
                m_oPPT = new Microsoft.Office.Interop.PowerPoint.Application();

                // Show PowerPoint to the user.
                m_oPPT.Visible = Microsoft.Office.Core.MsoTriState.msoTrue;

                m_objPresSet = m_oPPT.Presentations;

                //open the presentation            
                m_objPres = m_objPresSet.Open(
                    m_openPpt,
                    Microsoft.Office.Core.MsoTriState.msoFalse,
                    Microsoft.Office.Core.MsoTriState.msoTrue,
                    Microsoft.Office.Core.MsoTriState.msoFalse);

                m_objPres.SlideShowSettings.Run();
            }
        }

        private void closeBtn_click(object sender, RoutedEventArgs e) {
            if (m_openPpt == null)
                return;
            else
                m_openPpt = null;
        }
    }
}
