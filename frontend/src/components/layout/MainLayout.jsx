import { Header } from './Header';

export function MainLayout({ children }) {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <Header />
      <main className="flex-grow container mx-auto p-4 md:p-8">
        {children}
      </main>
    </div>
  );
}