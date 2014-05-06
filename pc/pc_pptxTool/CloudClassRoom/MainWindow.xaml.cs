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
using System.Net.Sockets;
using System.Net;

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
        Microsoft.Office.Interop.PowerPoint.Application     m_oPPT = null;
        Microsoft.Office.Interop.PowerPoint.Presentations   m_objPresSet = null;
        Microsoft.Office.Interop.PowerPoint.Presentation    m_objPres = null;

        // a random string generator
        private static Random m_random = new Random ((int)DateTime.Now.Ticks);

        private string generateRandomString(int size) {
            StringBuilder builder = new StringBuilder();
            char ch;
            for (int i = 0; i < size; i++) {
                ch = Convert.ToChar (Convert.ToInt32 (Math.Floor(26 * m_random.NextDouble() + 65)));
                builder.Append (ch);
            }

            return builder.ToString ();
        }

        public MainWindow () {
            InitializeComponent ();
            m_openPpt = null;            
        }

        private void exportCurrentSlide () {

            string fName = System.IO.Path.GetTempPath() + generateRandomString(13) + ".jpg";

            try {
                int idx = m_objPres.SlideShowWindow.View.Slide.SlideIndex;
                m_objPres.SlideShowWindow.View.Slide.Export (fName, "jpg", 1024, 768);

                string toSend = "UPDATE_FILE\n" + fName.Length.ToString () + ":" + fName + "\nEND";
                Console.WriteLine (toSend);
                sendUpdate(toSend);
            } catch (Exception e) {
                Console.WriteLine ("Exception happens");
            }
        }

        private void sendUpdate(string data) {

            try {
                Int32 port = 5566;
                TcpClient client = new TcpClient("localhost", port);

                Byte[] raw = Encoding.ASCII.GetBytes (data);
                NetworkStream stream = client.GetStream();
                stream.Write(raw, 0, raw.Length);
                stream.Close();
                client.Close();
            } catch (Exception e){
                Console.WriteLine("Exception happens");
            }
        }
        
        private void prevBtn_Click(object sender, RoutedEventArgs e) {
            if (m_openPpt == null)
                return;

            try
            {
                m_objPres.SlideShowWindow.View.Previous();
                exportCurrentSlide();
            }
            catch (Exception ee)
            {
                Console.WriteLine("It's the first slide already");
            }
        }

        private void nextBtn_Click(object sender, RoutedEventArgs e) {

            if (m_openPpt == null)
                return;
            try {
                m_objPres.SlideShowWindow.View.Next ();
                exportCurrentSlide ();
            }
            catch (Exception ee) {
                Console.WriteLine ("No more slides");

                if (m_oPPT != null)
                    m_oPPT.Quit ();
                
                m_oPPT = null;
                m_objPresSet = null;
                m_objPres = null;

                Environment.Exit(0);
            }
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

        private void closeBtn_click (object sender, RoutedEventArgs e) {
            
            if (m_oPPT != null)
                m_oPPT.Quit ();

            m_oPPT = null;
            m_objPresSet = null;
            m_objPres = null;
            
            Environment.Exit (0);           
        }
    }
}
