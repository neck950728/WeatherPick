import { Route, Routes } from 'react-router-dom';
import Header from './components/Header/Header';
import Home from './pages/Home/Home';
import styles from './App.module.scss';

function App() {
  return (
    <>
      <Header />
      <main className={styles.pageContainer}>
        <Routes>
          <Route path="/" element={<Home />}></Route>
        </Routes>
      </main>
    </>
  )
}

export default App