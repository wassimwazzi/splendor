class Flyweight:
    """
    Flyweight wrapper class
    """

    def __init__(self, decorated):
        self._decorated = decorated
        self._flyweights = {}

    def instance(self, id, *args, **kwargs):
        """
        Returns the flyweight instance. Upon its first call, it creates a new instance of the decorated class and calls its `__init__` method.
        On all subsequent calls, the already created instance is returned.
        """
        try:
            return self._flyweights[id]
        except KeyError:
            self._flyweights[id] = self._decorated(id=id, *args, **kwargs)
            return self._flyweights[id]

    def delete_all(self):
        print("before delete: ", self._flyweights)
        self._flyweights = {}
        print("Reset flyweights: ", self._flyweights)

    def __call__(self):
        raise TypeError('Flyweights must be accessed through `instance()`.')

    def __instancecheck__(self, inst):
        return isinstance(inst, self._decorated)

    def __getattr__(self, name):
        return getattr(self._decorated, name)

    @property
    def flyweights(self):
        return self._flyweights
