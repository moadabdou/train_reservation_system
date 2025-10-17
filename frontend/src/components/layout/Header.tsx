import { Train } from 'lucide-react'; // A nice icon library included with shadcn

export function Header() {
  return (
    <header className="border-b">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <div className="flex items-center gap-2">
          <Train className="h-6 w-6 text-primary" />
          <h1 className="text-xl font-bold tracking-tight">TrainReserve</h1>
        </div>
        {/* We'll add Login/Register buttons here later in Slice 2 */}
      </div>
    </header>
  );
}